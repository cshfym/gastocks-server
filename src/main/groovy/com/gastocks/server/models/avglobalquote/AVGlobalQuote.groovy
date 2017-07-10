package com.gastocks.server.models.avglobalquote

import com.gastocks.server.models.IQuote
import groovy.transform.ToString
import org.joda.time.DateTime

@ToString
class AVGlobalQuote implements IQuote {

    String symbol
    String exchangeName
    Double latestPrice
    Double currentTradingDayOpen
    Double currentTradingDayHigh
    Double currentTradingDayLow
    Double previousTradingDayClose
    Double priceChange
    Float priceChangePercentage
    Integer volume
    DateTime lastUpdated

}
