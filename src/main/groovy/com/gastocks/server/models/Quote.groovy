package com.gastocks.server.models

import groovy.transform.ToString
import org.joda.time.DateTime

@ToString
class Quote {

    String symbol
    String exchangeName
    Double latestPrice
    Double currentTradingDayOpen
    Double currentTradingDayHigh
    Double currentTradingDayLow
    Double previousTradingDayClose
    Double priceChange
    Double priceChangePercentage
    Integer volume
    DateTime lastUpdated

}
