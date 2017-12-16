package com.gastocks.server.services.technical

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.technical.TechnicalDataWrapper
import com.gastocks.server.models.technical.request.EMVRequestParameters
import com.gastocks.server.models.technical.request.RSIRequestParameters
import com.gastocks.server.models.technical.response.EMVTechnicalData
import com.gastocks.server.models.technical.response.RSITechnicalData
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Ease of movement
 */
@Slf4j
@Service
class EMVService {

    @Autowired
    TechnicalToolsService technicalToolsService


    void buildEMVTechnicalData(List<TechnicalDataWrapper> technicalWrapperDataList, List<PersistableQuote> quoteData, EMVRequestParameters parameters) {

        EMVTechnicalData previousEMVTechnicalData

        quoteData.eachWithIndex { quote, ix ->

            def technicalWrapper = technicalWrapperDataList.find { it.quoteDate == quote.quoteDate }

            technicalWrapper.emvTechnicalData = new EMVTechnicalData()

            EMVTechnicalData emvData = technicalWrapper.emvTechnicalData

            emvData.with {
                periods = parameters ? parameters.periods : 14
                volume = quote.volume
                easeOfMovement = 0.0d
                easeOfMovementSMA = 0.0d
                smaCrossoverNegative = false
                smaCrossoverPositive = false
                easyMovement = false
                difficultMovement = false
                volumeTriggerPercentage = parameters.volumeTriggerPercentage
                highLowTriggerPercentage = parameters.highLowTriggerPercentage
            }

            if (ix == 0) {
                previousEMVTechnicalData = emvData
                return
            }

            // ((H + L)/2 - (Prior H + Prior L)/2) (26.55 + 26.22) / 2 minus (27.0 + 25.77) / 2
            double todayDifference = ((quote.dayHigh + quote.dayLow) / 2.0d)
            double yesterdayDifference = ((quoteData[ix - 1].dayHigh + quoteData[ix - 1].dayLow) / 2.0d)
            double distanceMoved = todayDifference - yesterdayDifference

            // ((V/boxRatio)/(H - L))
            double boxRatio = ((quote.volume / parameters.boxRatio) / (quote.dayHigh - quote.dayLow))

            // distanceMoved / boxRatio
            emvData.boxRatio = boxRatio.round(3)
            emvData.easeOfMovement = (boxRatio > 0) ? (distanceMoved / boxRatio).round(3) : 0.0d

            previousEMVTechnicalData = emvData
        }
    }

    void buildEMVSignalData(List<TechnicalDataWrapper> technicalDataList, EMVRequestParameters parameters) {

        technicalDataList.eachWithIndex { technicalData, ix ->

            EMVTechnicalData emvTechnicalData = technicalDataList[ix].emvTechnicalData
            EMVTechnicalData emvTechnicalDataYesterday = technicalDataList[ix - 1].emvTechnicalData

            if (ix > 0) {

                emvTechnicalData.easeOfMovementSMA = technicalToolsService.calculateEMA(emvTechnicalData.easeOfMovement, emvTechnicalDataYesterday.easeOfMovementSMA, parameters.periods)

                // SMA Crossover Positive - SMA crossing over from negative to positive
                if ((emvTechnicalData.easeOfMovementSMA >= 0.0) && (emvTechnicalDataYesterday.easeOfMovementSMA < 0.0)) {
                    emvTechnicalData.smaCrossoverPositive = true
                    emvTechnicalData.smaCrossoverNegative = false
                }
                // SMA Crossover Negative - SMA crossing over from positive to negative
                if ((emvTechnicalData.easeOfMovementSMA < 0.0) && (emvTechnicalDataYesterday.easeOfMovementSMA >= 0.0)) {
                    emvTechnicalData.smaCrossoverNegative = true
                    emvTechnicalData.smaCrossoverPositive = false
                }

                // Establish baselines for relative comparison - high/low range, and average volume over the period specified (typically 14 days)
                double averagePeriodHighLowDifference = calculateAveragePeriodHighLowDifference(technicalDataList, parameters, ix)
                double averagePeriodVolume = calculateAveragePeriodVolume(technicalDataList, parameters, ix)

                // Trigger percentage = percent above average when considering easy or difficult movement.
                // Example: average period volume is 500,000; trigger value would be 0.10 above 500,000 or 550,000
                double triggerVolume = averagePeriodVolume * parameters.volumeTriggerPercentage
                double triggerPeriodHighLow = averagePeriodHighLowDifference * parameters.highLowTriggerPercentage

                // Easy movement - volume is normal, but high/low range is higher than trigger
                if ((technicalData.volume < triggerVolume) && ((technicalData.high - technicalData.low) >= triggerPeriodHighLow)) {
                    emvTechnicalData.easyMovement = true
                    /* log.info("Determined easy movement with volume [${emvTechnicalData.volume}] and high-low [${(technicalData.high - technicalData.low)}], " +
                            "with triggers [${triggerVolume}] and [${triggerPeriodHighLow}] on [${technicalData.quoteDate}]") */
                }

                // Difficult movement - volume is high, but high/low range is lower than trigger
                if ((technicalData.volume >= triggerVolume) && ((technicalData.high - technicalData.low) < triggerPeriodHighLow)) {
                    emvTechnicalData.difficultMovement = true
                    /* log.info("Determined difficult movement with volume [${emvTechnicalData.volume}] and high-low [${(technicalData.high - technicalData.low)}], " +
                            "with triggers [${triggerVolume}] and [${triggerPeriodHighLow}] on [${technicalData.quoteDate}]") */
                }

            }
        }
    }

    static double calculateAveragePeriodHighLowDifference(List<TechnicalDataWrapper> technicalDataList, EMVRequestParameters emvRequestParameters, int currentIndex) {

        int floor = currentIndex - emvRequestParameters.periods
        if (floor < 0) { floor = 0 }

        int count = 0
        double periodTotal = 0.0d

        for (int i = floor; i < currentIndex; i++) {
            periodTotal += (technicalDataList[i].high - technicalDataList[i].low).round(3)
            count++
        }

        (count > 0) ? (periodTotal / count).round(3) : 0.0d
    }

    static double calculateAveragePeriodVolume(List<TechnicalDataWrapper> technicalDataList, EMVRequestParameters emvRequestParameters, int currentIndex) {

        int floor = currentIndex - emvRequestParameters.periods
        if (floor < 0) { floor = 0 }

        int count = 0
        double periodTotal = 0.0d

        for (int i = floor; i < currentIndex; i++) {
            periodTotal += technicalDataList[i].volume
            count++
        }

        (count > 0) ? (periodTotal / count).round(3) : 0.0d
    }

}