package com.gastocks.server.models.technical

class TechnicalDataWrapper {

    Date quoteDate

    double _52WeekAverage
    double _26WeekAverage
    double _12WeekAverage
    double _6WeekAverage
    double _3WeekAverage
    double _1WeekAverage

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

    boolean priceChangeFromLastQuote

    MACDTechnicalData macdTechnicalData

}
