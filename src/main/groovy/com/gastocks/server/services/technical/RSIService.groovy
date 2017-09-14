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


    void buildRSITechnicalData(List<TechnicalDataWrapper> technicalWrapperDataList, List<PersistableQuote> quoteData, RSIRequestParameters parameters) {

        RSITechnicalData previousRSITechnicalData

        quoteData.eachWithIndex { quote, ix ->

            def technicalWrapper = technicalWrapperDataList.find { it.quoteDate == quote.quoteDate }

            technicalWrapper.rsiTechnicalData = new RSITechnicalData(interval: parameters.interval, overBoughtLine: parameters.overBoughtLine,
                    overSoldLine: parameters.overSoldLine, averagePriceGain: 0.0d, averagePriceLoss: 0.0d)

            RSITechnicalData rsiData = technicalWrapper.rsiTechnicalData

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

    void buildRSISignalData(List<TechnicalDataWrapper> technicalDataList) {

        technicalDataList.eachWithIndex { technicalData, ix ->

            RSITechnicalData rsiData = technicalDataList[ix].rsiTechnicalData
            RSITechnicalData rsiDataYesterday = technicalDataList[ix-1].rsiTechnicalData



        }
    }

    double calculateFirstIntervalAverageGain(int startIndex, int endIndex, List<TechnicalDataWrapper> technicalWrapperDataList) {
        double intervalGainTotal = 0.0d
        for (int i = startIndex; i < (endIndex + 1); i++) { intervalGainTotal += technicalWrapperDataList[i].rsiTechnicalData.priceGain }
        (intervalGainTotal / (endIndex - startIndex)).round(4)
    }

    double calculateFirstIntervalAverageLoss(int startIndex, int endIndex, List<TechnicalDataWrapper> technicalWrapperDataList) {
        double intervalLossTotal = 0.0d
        for (int i = startIndex; i < (endIndex + 1); i++) { intervalLossTotal += technicalWrapperDataList[i].rsiTechnicalData.priceLoss }
        (intervalLossTotal / (endIndex - startIndex)).round(4)
    }

    double calculateSubsequentIntervalAverage(int interval, double previousAverage, double current) {
        (double)(((previousAverage * (interval - 1)) + current) / interval).round(4)
    }

    double calculateRelativeStrength(double averageGain, double averageLoss) {
        averageLoss ? ((double)(averageGain / averageLoss)).round(4) : 0.00d
    }

    double calculateRelativeStrengthIndex(double relativeStrength) {
        (100 - (100 / (1 + relativeStrength))).round(4)
    }
}
