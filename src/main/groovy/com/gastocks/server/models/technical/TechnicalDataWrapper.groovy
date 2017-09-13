package com.gastocks.server.models.technical

import com.gastocks.server.models.technical.response.MACDTechnicalData
import com.gastocks.server.models.technical.response.RSITechnicalData
import com.gastocks.server.models.technical.response.TechnicalQuoteMetadata
import com.gastocks.server.models.technical.response.TechnicalQuoteParameters

// TODO Get rid of this class?
class TechnicalDataWrapper {

    Date quoteDate

    TechnicalQuoteParameters quoteParameters

    TechnicalQuoteMetadata quoteMetadata

    MACDTechnicalData macdTechnicalData

    RSITechnicalData rsiTechnicalData

}
