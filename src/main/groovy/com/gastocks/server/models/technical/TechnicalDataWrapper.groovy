package com.gastocks.server.models.technical

import com.gastocks.server.models.technical.response.EMVTechnicalData
import com.gastocks.server.models.technical.response.MACDTechnicalData
import com.gastocks.server.models.technical.response.OBVTechnicalData
import com.gastocks.server.models.technical.response.RSITechnicalData
import com.gastocks.server.models.technical.response.TechnicalQuoteMetadata
import com.gastocks.server.models.technical.response.TechnicalQuoteParameters

class TechnicalDataWrapper {

    Date quoteDate

    double price
    double high
    double low
    int volume

    TechnicalQuoteParameters quoteParameters

    TechnicalQuoteMetadata quoteMetadata

    MACDTechnicalData macdTechnicalData

    RSITechnicalData rsiTechnicalData

    OBVTechnicalData onBalanceVolumeData

    EMVTechnicalData emvTechnicalData
}
