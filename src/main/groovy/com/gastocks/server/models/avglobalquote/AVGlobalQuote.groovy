package com.gastocks.server.models.avglobalquote

import com.gastocks.server.models.IQuote
import groovy.transform.ToString
import org.joda.time.DateTime

@ToString
class AVGlobalQuote implements IQuote {

    String symbol
    String exchangeName
    double latestPrice
    double currentTradingDayOpen
    double currentTradingDayHigh
    double currentTradingDayLow
    double previousTradingDayClose
    double priceChange
    float priceChangePercentage
    Integer volume
    DateTime lastUpdated

}
