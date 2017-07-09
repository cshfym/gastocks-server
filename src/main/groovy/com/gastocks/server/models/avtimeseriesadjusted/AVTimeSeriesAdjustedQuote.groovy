package com.gastocks.server.models.avtimeseriesadjusted

import groovy.transform.ToString
import org.joda.time.DateTime

@ToString
class  AVTimeSeriesAdjustedQuote {

    String symbol
    List<AVTimeSeriesAdjustedDay> dailyQuoteList

}
