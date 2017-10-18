package com.gastocks.server.models.technical

import com.gastocks.server.models.technical.response.MACDTechnicalData
import com.gastocks.server.models.technical.response.OBVTechnicalData
import com.gastocks.server.models.technical.response.RSITechnicalData
import com.gastocks.server.models.technical.response.TechnicalQuoteMetadata
import com.gastocks.server.models.technical.response.TechnicalQuoteParameters

// TODO Get rid of this class?
class TechnicalDataWrapper {

    Date quoteDate

    double price

    TechnicalQuoteParameters quoteParameters

    TechnicalQuoteMetadata quoteMetadata

    MACDTechnicalData macdTechnicalData

    RSITechnicalData rsiTechnicalData

    OBVTechnicalData onBalanceVolumeData

}
