package com.gastocks.server.services.technical

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.technical.TechnicalDataWrapper
import com.gastocks.server.models.technical.request.OBVRequestParameters
import com.gastocks.server.models.technical.response.OBVTechnicalData
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class OBVService {

    static double OBV_RATIO_ADJUSTMENT = 1000 // i.e. express OBV in units of 1,000.

    void buildOBVTechnicalData(List<TechnicalDataWrapper> technicalWrapperDataList, List<PersistableQuote> quoteData, OBVRequestParameters requestParameters) {

        OBVTechnicalData previousOnBalanceVolumeData

        quoteData.eachWithIndex { quote, ix ->

            def technicalWrapper = technicalWrapperDataList.find { it.quoteDate == quote.quoteDate }

            technicalWrapper.onBalanceVolumeData = new OBVTechnicalData()

            OBVTechnicalData onBalanceVolumeData = technicalWrapper.onBalanceVolumeData

            if (ix == 0) {
                onBalanceVolumeData.onBalanceVolume = 0
                previousOnBalanceVolumeData = onBalanceVolumeData
                return
            }

            double priceChange = quote.price - quoteData[ix-1].price

            double adjustedVolume = ((double) quote.volume /  OBV_RATIO_ADJUSTMENT).round(2)

            if (priceChange == 0) {
                onBalanceVolumeData.onBalanceVolume = previousOnBalanceVolumeData.onBalanceVolume
            } else if (priceChange > 0) {
                onBalanceVolumeData.onBalanceVolume = previousOnBalanceVolumeData.onBalanceVolume + adjustedVolume
            } else {
                onBalanceVolumeData.onBalanceVolume = previousOnBalanceVolumeData.onBalanceVolume - adjustedVolume
            }

            onBalanceVolumeData.onBalanceVolumeShort = TechnicalToolsService.calculateEMA(onBalanceVolumeData.onBalanceVolume,
                    previousOnBalanceVolumeData.onBalanceVolume, requestParameters.onBalanceVolumeShortPeriod)

            onBalanceVolumeData.onBalanceVolumeLong = TechnicalToolsService.calculateEMA(onBalanceVolumeData.onBalanceVolume,
                    previousOnBalanceVolumeData.onBalanceVolume, requestParameters.onBalanceVolumeLongPeriod)

            previousOnBalanceVolumeData = onBalanceVolumeData
        }

    }

}
