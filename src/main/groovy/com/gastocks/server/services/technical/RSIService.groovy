package com.gastocks.server.services.technical

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.technical.request.RSIRequestParameters
import com.gastocks.server.models.technical.response.RSITechnicalData
import com.gastocks.server.models.technical.TechnicalDataWrapper
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class RSIService {

    /* Ignore the first N quote periods to allow the RSI to smooth out */
    final static int IGNORE_RSI_INITIAL_PERIODS = 15

    void buildRSITechnicalData(List<TechnicalDataWrapper> technicalWrapperDataList, List<PersistableQuote> quoteData, RSIRequestParameters parameters) {

        RSITechnicalData previousRSITechnicalData

        quoteData.eachWithIndex { quote, ix ->

            def technicalWrapper = technicalWrapperDataList.find { it.quoteDate == quote.quoteDate }

            technicalWrapper.rsiTechnicalData = new RSITechnicalData()

            RSITechnicalData rsiData = technicalWrapper.rsiTechnicalData

            rsiData.with {
                interval = parameters.interval
                overBoughtLine = parameters.overBoughtLine
                overSoldLine = parameters.overSoldLine
                averagePriceGain = 0.0d
                averagePriceLoss = 0.0d
                periodsOverBought = 0
                periodsOverSold = 0
            }

            if (ix == 0) {
                previousRSITechnicalData = rsiData
                return
            }

            // Capture delta in price
            double priceChange = (quote.price - quoteData[ix - 1].price).round(4)
            if (priceChange >= 0) {
                rsiData.priceGain = priceChange.round(4)
            } else {
                rsiData.priceLoss = Math.abs(priceChange).round(4)
            }

            if (ix < parameters.interval) {
                // Not all data is available up to the requested interval at this point, but calculate averageGain/Loss anyway.
                rsiData.averagePriceGain = calculateFirstIntervalAverageGain(0, ix, technicalWrapperDataList)
                rsiData.averagePriceLoss = calculateFirstIntervalAverageLoss(0, ix, technicalWrapperDataList)
            } else if (ix == parameters.interval) {
                // First RSI average gain/loss calculations are simple average
                rsiData.averagePriceGain = calculateFirstIntervalAverageGain((ix - parameters.interval), ix, technicalWrapperDataList)
                rsiData.averagePriceLoss = calculateFirstIntervalAverageLoss((ix - parameters.interval), ix, technicalWrapperDataList)
                rsiData.relativeStrength = calculateRelativeStrength(rsiData.averagePriceGain, rsiData.averagePriceLoss)
                rsiData.relativeStrengthIndex = calculateRelativeStrengthIndex(rsiData.relativeStrength)
            } else if (ix > parameters.interval) {
                // Subsequent RSI average gain/loss calculations are a derivative: ((Previous Gain * (Interval - 1) + Current Gain) / Interval)
                rsiData.averagePriceGain = calculateSubsequentIntervalAverage(parameters.interval, previousRSITechnicalData.averagePriceGain, rsiData.priceGain)
                rsiData.averagePriceLoss = calculateSubsequentIntervalAverage(parameters.interval, previousRSITechnicalData.averagePriceLoss, rsiData.priceLoss)
                rsiData.relativeStrength = calculateRelativeStrength(rsiData.averagePriceGain, rsiData.averagePriceLoss)
                rsiData.relativeStrengthIndex = calculateRelativeStrengthIndex(rsiData.relativeStrength)
            }

            /*
            log.info("Calculated RSI of [${rsiData.relativeStrengthIndex}] RS: [${rsiData.relativeStrength}] Gain: [${rsiData.priceGain}] Loss: [${rsiData.priceLoss}] " +
                    "AvgGain: [${rsiData.averagePriceGain}] AvgLoss: [${rsiData.averagePriceLoss}]" +
                    ", previous rsiData Gain: [${previousRSITechnicalData.averagePriceGain}], Loss: [${previousRSITechnicalData.averagePriceLoss}]" +
                    " for [${quote.symbol}]-[${quote.price}]-[${quote.quoteDate}]")
            */

            previousRSITechnicalData = rsiData
        }
    }

    static void buildRSISignalData(List<TechnicalDataWrapper> technicalDataList) {

        technicalDataList.eachWithIndex { TechnicalDataWrapper technicalData, ix ->

            RSITechnicalData rsiData = technicalDataList[ix].rsiTechnicalData
            RSITechnicalData rsiDataYesterday = technicalDataList[ix-1].rsiTechnicalData

            if ((ix < IGNORE_RSI_INITIAL_PERIODS) || (technicalData.price == 0)) { return }

            // Set overbought, oversold indicators
            if (rsiData.relativeStrengthIndex > rsiData.overBoughtLine) {
                rsiData.overBought = true
            }
            if (rsiData.relativeStrengthIndex < rsiData.overSoldLine) {
                rsiData.overSold = true
            }

            // Overbought crossover negative, i.e. RSI crosses below the 70 line (70.5 to 69.5, for example)
            if (rsiData.overBought && !rsiDataYesterday.overBought) { rsiData.overBoughtCrossoverPositive = true }

            // Overbought crossover positive, i.e. RSI crosses above the 70 line (69.8 to 70.3, for example)
            if (!rsiData.overBought && rsiDataYesterday.overBought) { rsiData.overBoughtCrossoverNegative = true }

            // Oversold crossover negative, i.e. RSI crosses below the 30 line (30.5 to 29.5, for example)
            if (rsiData.overSold && !rsiDataYesterday.overSold) { rsiData.overSoldCrossoverNegative = true }

            // Oversold crossover positive, i.e. RSI crosses above the 30 line (29.5 to 30.5, for example)
            if (!rsiData.overSold && rsiDataYesterday.overSold) { rsiData.overSoldCrossoverPositive = true }

            // Middle crossovers
            if (rsiData.relativeStrengthIndex >= 50.0d && rsiDataYesterday.relativeStrengthIndex < 50.0d) { rsiData.centerLineCrossoverPositive = true }
            if (rsiData.relativeStrengthIndex <= 50.0d && rsiDataYesterday.relativeStrengthIndex > 50.0d) { rsiData.centerLineCrossoverNegative = true }

            // Calculate periods overbought/oversold
            calculatePeriodsOverBought(technicalDataList, ix)
            calculatePeriodsOverSold(technicalDataList, ix)
        }
    }

    static void calculatePeriodsOverBought(List<TechnicalDataWrapper> technicalDataList, int currentIndex) {

        if (!technicalDataList[currentIndex].rsiTechnicalData.overBought) {  // Not currently overbought
            technicalDataList[currentIndex].rsiTechnicalData.periodsOverBought = 0
            return
        }

        if (currentIndex == 0) { return }

        if (technicalDataList[currentIndex - 1].rsiTechnicalData.overBought) {
            technicalDataList[currentIndex].rsiTechnicalData.periodsOverBought = technicalDataList[currentIndex - 1].rsiTechnicalData.periodsOverBought + 1
        }
    }

    static void calculatePeriodsOverSold(List<TechnicalDataWrapper> technicalDataList, int currentIndex) {

        if (!technicalDataList[currentIndex].rsiTechnicalData.overSold) {  // Not currently overbought
            technicalDataList[currentIndex].rsiTechnicalData.periodsOverSold = 0
            return
        }

        if (currentIndex == 0) { return }

        if (technicalDataList[currentIndex - 1].rsiTechnicalData.overSold) {
            technicalDataList[currentIndex].rsiTechnicalData.periodsOverSold = technicalDataList[currentIndex - 1].rsiTechnicalData.periodsOverSold + 1
        }
    }

    static double calculateFirstIntervalAverageGain(int startIndex, int endIndex, List<TechnicalDataWrapper> technicalWrapperDataList) {
        double intervalGainTotal = 0.0d
        for (int i = startIndex; i < (endIndex + 1); i++) { intervalGainTotal += technicalWrapperDataList[i].rsiTechnicalData.priceGain }
        (intervalGainTotal / (endIndex - startIndex)).round(4)
    }

    static double calculateFirstIntervalAverageLoss(int startIndex, int endIndex, List<TechnicalDataWrapper> technicalWrapperDataList) {
        double intervalLossTotal = 0.0d
        for (int i = startIndex; i < (endIndex + 1); i++) { intervalLossTotal += technicalWrapperDataList[i].rsiTechnicalData.priceLoss }
        (intervalLossTotal / (endIndex - startIndex)).round(4)
    }

    static double calculateSubsequentIntervalAverage(int interval, double previousAverage, double current) {
        (double)(((previousAverage * (interval - 1)) + current) / interval).round(4)
    }

    static double calculateRelativeStrength(double averageGain, double averageLoss) {
        averageLoss ? ((double)(averageGain / averageLoss)).round(4) : 0.00d
    }

    static double calculateRelativeStrengthIndex(double relativeStrength) {
        (100 - (100 / (1 + relativeStrength))).round(4)
    }
}
