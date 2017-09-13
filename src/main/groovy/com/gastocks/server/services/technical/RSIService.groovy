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

        quoteData.eachWithIndex { quote, ix ->

            def technicalWrapper = technicalWrapperDataList.find { it.quoteDate == quote.quoteDate }

            technicalWrapper.rsiTechnicalData = new RSITechnicalData(interval: parameters.interval, averagePriceGain: 0.0d, averagePriceLoss: 0.0d)

            if (ix == 0) { return }

            // Capture delta in price
            double priceChange = quote.price - quoteData[ix - 1].price
            if (priceChange >= 0) {
                technicalWrapper.rsiTechnicalData.priceGain = priceChange.round(2)
            } else {
                technicalWrapper.rsiTechnicalData.priceLoss = Math.abs(priceChange).round(2)
            }

            if (ix < parameters.interval) { return }

            // N quote intervals are available, begin calculating RSI values

            technicalWrapper.rsiTechnicalData.averagePriceGain = calculateIntervalAverageGain((ix - parameters.interval), ix, technicalWrapperDataList)
            technicalWrapper.rsiTechnicalData.averagePriceLoss = calculateIntervalAverageLoss((ix - parameters.interval), ix, technicalWrapperDataList)

            technicalWrapper.rsiTechnicalData.relativeStrength =
                    technicalWrapper.rsiTechnicalData.averagePriceLoss ?
                    (technicalWrapper.rsiTechnicalData.averagePriceGain / technicalWrapper.rsiTechnicalData.averagePriceLoss).round(2) : 0.00d

            technicalWrapper.rsiTechnicalData.relativeStrengthIndex = (100 - (100 / (1 + technicalWrapper.rsiTechnicalData.relativeStrength))).round(2)

        }
    }

    double calculateIntervalAverageGain(int startIndex, int endIndex, List<TechnicalDataWrapper> technicalWrapperDataList) {

        double intervalGainTotal = 0.0d
        for (int i = startIndex; i < endIndex; i++) { intervalGainTotal += technicalWrapperDataList[i].rsiTechnicalData.priceGain }
        (intervalGainTotal / (endIndex - startIndex)).round(2)
    }

    double calculateIntervalAverageLoss(int startIndex, int endIndex, List<TechnicalDataWrapper> technicalWrapperDataList) {

        double intervalLossTotal = 0.0d
        for (int i = startIndex; i < endIndex; i++) { intervalLossTotal += technicalWrapperDataList[i].rsiTechnicalData.priceLoss }
        (intervalLossTotal / (endIndex - startIndex)).round(2)
    }

}
