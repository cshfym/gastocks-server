package com.gastocks.server.models.intrinio

class IntrinioExchangePriceQuote {

    String date
    String ticker

    double open
    double high
    double low
    double close

    int volume

    double adjClose
    double adjHigh
    double adjLow
    double adjOpen
    double adjVolume

    double exDividend
    String figi
    String figiTicker
    double splitRatio

}
