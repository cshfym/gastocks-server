package com.gastocks.server.models.symbol

/**
 * EnhancedSymbol extends {@link Symbol}
 * Includes basic quote data, used for quote searching
 */
class EnhancedSymbol extends Symbol {

    double maximumQuotePrice
    double minimumQuotePrice
    double averageQuotePrice

}
