package com.gastocks.server.models.technical

import com.gastocks.server.models.quote.Quote


/**
 * Represents a non-persistent, externally-facing TechnicalQuote for a single symbol.
 */
class TechnicalQuote extends Quote {

    int shortParameter
    int longParameter
    Double emaShort
    Double emaLong
    Double macd
    Double macdSignalLine
    Double macdHist
    boolean centerCrossoverPositive
    boolean centerCrossoverNegative
    boolean signalCrossoverPositive
    boolean signalCrossoverNegative

}
