package com.gastocks.server.services.technical

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.technical.TechnicalDataWrapper
import com.gastocks.server.models.technical.request.OBVRequestParameters
import com.gastocks.server.models.technical.response.OBVTechnicalData
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class OBVService {

    void buildOBVTechnicalData(List<TechnicalDataWrapper> technicalWrapperDataList, List<PersistableQuote> quoteData, OBVRequestParameters requestParameters) {

        OBVTechnicalData previousOnBalanceVolume

        quoteData.eachWithIndex { quote, ix ->

            def technicalWrapper = technicalWrapperDataList.find { it.quoteDate == quote.quoteDate }

            technicalWrapper.onBalanceVolumeData = new OBVTechnicalData()

            OBVTechnicalData onBalanceVolumeData = technicalWrapper.onBalanceVolumeData

            if (ix == 0) {
                onBalanceVolumeData.onBalanceVolume = 0
                previousOnBalanceVolume = onBalanceVolumeData
                return
            }

            double priceChange = quote.priceChange - quoteData[ix-1].price

            if (priceChange == 0) {
                onBalanceVolumeData.onBalanceVolume = previousOnBalanceVolume.onBalanceVolume
            } else if (priceChange > 0) {
                onBalanceVolumeData.onBalanceVolume += quote.volume
            } else {
                onBalanceVolumeData.onBalanceVolume -= quote.volume
            }

            onBalanceVolumeData.onBalanceVolumeShort = TechnicalToolsService.calculateEMA(onBalanceVolumeData.onBalanceVolume,
                    previousOnBalanceVolume.onBalanceVolume, requestParameters.onBalanceVolumeShortPeriod)

            onBalanceVolumeData.onBalanceVolumeLong = TechnicalToolsService.calculateEMA(onBalanceVolumeData.onBalanceVolume,
                    previousOnBalanceVolume.onBalanceVolume, requestParameters.onBalanceVolumeLongPeriod)

            previousOnBalanceVolume = onBalanceVolumeData

        }

    }

}
