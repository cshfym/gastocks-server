package com.gastocks.server.models.quote


/**
 * Represents a non-persistent, externally-facing quote for a single symbol.
 */
class Quote {

    Date quoteDate
    String symbol
    String exchangeName
    double price
    double open
    double high
    double low
    Integer volume

}
