package com.gastocks.server.models.avtimeseriesadjusted

import groovy.transform.ToString
import org.joda.time.DateTime

@ToString
class AVTimeSeriesAdjustedQuote {

    String symbol
    String exchangeName
    Double dayOpen
    Double dayHigh
    Double dayLow
    Double adjustedClose
    Integer volume
    Double dividend
    Double splitCoefficient
    DateTime lastUpdated

}
