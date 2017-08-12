package com.gastocks.server.models.quote


/**
 * Represents a non-persistent, externally-facing EMAQuote for a single symbol.
 */
class EMAQuote extends Quote {

    int shortParameter
    int longParameter
    Double emaShort
    Double emaLong

}
