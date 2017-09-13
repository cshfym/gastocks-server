package com.gastocks.server.models.technical

import com.gastocks.server.models.quote.Quote


/**
 * Represents a non-persistent, externally-facing TechnicalQuote for a single symbol.
 */
class TechnicalQuote extends Quote {

    TechnicalQuoteParameters quoteParameters

    TechnicalQuoteMetadata quoteMetadata

    MACDTechnicalData macdParameters

    RSITechnicalData rsiParameters
}
