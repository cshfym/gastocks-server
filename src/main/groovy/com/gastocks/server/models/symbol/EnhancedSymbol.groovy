package com.gastocks.server.models.symbol

import com.gastocks.server.models.domain.PersistableSymbolExtended

/**
 * EnhancedSymbol extends {@link Symbol}
 * Includes basic quote data, used for quote searching
 */
class EnhancedSymbol extends Symbol {

    double maximumQuotePrice
    double minimumQuotePrice
    double averageQuotePrice

    int quotePeriods

    Date newestQuoteDate
    Date oldestQuoteDate

}
