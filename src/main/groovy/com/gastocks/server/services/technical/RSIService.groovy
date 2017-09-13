package com.gastocks.server.services.technical

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.simulation.RSIRequestParameters
import com.gastocks.server.models.technical.RSITechnicalData
import com.gastocks.server.models.technical.TechnicalDataWrapper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class RSIService {

    @Autowired
    TechnicalToolsService technicalToolsService

    void buildRSITechnicalData(List<TechnicalDataWrapper> technicalWrapperDataList, List<PersistableQuote> quoteData, RSIRequestParameters parameters) {

        quoteData.eachWithIndex { quote, ix ->

            def technicalWrapper = technicalWrapperDataList.find { it.quoteDate == quote.quoteDate }

            if (ix == 0) {
                technicalWrapper.rsiTechnicalData = new RSITechnicalData(interval: parameters.interval, priceGain: 0, priceLoss: 0)
            } else {

                // Capture delta in price
                double priceChange = Math.abs(quote.price - quoteData[ix - 1].price)
                if (priceChange >= 0) {
                    technicalWrapper.rsiTechnicalData.priceGain = priceChange
                } else {
                    technicalWrapper.rsiTechnicalData.priceLoss = priceChange
                }

                // If N quote intervals are available, begin calculating RSI
                if ((ix - 1) >= parameters.interval) {

                }
            }
        }
    }

    double calculateIntervalAverageGain(int interval, int startIndex, int endIndex, List<TechnicalDataWrapper> technicalWrapperDataList) {

        double intervalAverage = 0.0d

        for (int i = startIndex; i < endIndex; i++) {

        }
    }
}
