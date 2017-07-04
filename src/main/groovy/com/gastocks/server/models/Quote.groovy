package com.gastocks.server.models

import org.joda.time.DateTime

class Quote {

    String symbol
    String exchangeName
    double latestPrice
    double currentTradingDayOpen
    double currentTradingDayHigh
    double currentTradingDayLow
    double previousTradingDayClose
    double priceChange
    double priceChangePercentage
    int volume
    DateTime lastUpdated

}
