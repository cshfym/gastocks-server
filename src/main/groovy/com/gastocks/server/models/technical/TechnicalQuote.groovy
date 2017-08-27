package com.gastocks.server.models.technical

import com.gastocks.server.models.quote.Quote


/**
 * Represents a non-persistent, externally-facing TechnicalQuote for a single symbol.
 */
class TechnicalQuote extends Quote {

    int shortParameter
    int longParameter
    double emaShort
    double emaLong
    double macd
    double macdSignalLine
    double macdHist
    boolean centerCrossoverPositive
    boolean centerCrossoverNegative
    boolean signalCrossoverPositive
    boolean signalCrossoverNegative

    double _52WeekAverage
    double _26WeekAverage
    double _12WeekAverage
    double _6WeekAverage
    double _3WeekAverage
    double _1WeekAverage

}
