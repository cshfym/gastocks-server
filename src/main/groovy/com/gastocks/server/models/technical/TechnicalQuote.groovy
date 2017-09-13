package com.gastocks.server.models.technical

import com.gastocks.server.models.quote.Quote


/**
 * Represents a non-persistent, externally-facing TechnicalQuote for a single symbol.
 */
class TechnicalQuote extends Quote {

    double emaShort
    double emaLong
    double macd
    double macdSignalLine
    double macdHist
    boolean centerCrossoverPositive
    boolean centerCrossoverNegative
    boolean signalCrossoverPositive
    boolean signalCrossoverNegative
    boolean priceChangeFromLastQuote

    double _52WeekHigh
    double _52WeekLow
    double _26WeekHigh
    double _26WeekLow
    double _12WeekHigh
    double _12WeekLow
    double _6WeekHigh
    double _6WeekLow
    double _3WeekHigh
    double _3WeekLow
    double _1WeekHigh
    double _1WeekLow

    double _52WeekAverage
    double _26WeekAverage
    double _12WeekAverage
    double _6WeekAverage
    double _3WeekAverage
    double _1WeekAverage

}
