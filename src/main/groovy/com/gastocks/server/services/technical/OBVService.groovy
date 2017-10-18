package com.gastocks.server.services.technical

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.technical.TechnicalDataWrapper
import com.gastocks.server.models.technical.response.OBVTechnicalData
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class OBVService {

    void buildOBVTechnicalData(List<TechnicalDataWrapper> technicalWrapperDataList, List<PersistableQuote> quoteData) {

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

            if (quote.priceChange == 0) {
                onBalanceVolumeData.onBalanceVolume = previousOnBalanceVolume.onBalanceVolume
            } else if (quote.priceChange > 0) {
                onBalanceVolumeData.onBalanceVolume += quote.volume
            } else {
                onBalanceVolumeData.onBalanceVolume -= quote.volume
            }

            previousOnBalanceVolume = onBalanceVolumeData

        }

    }

}
