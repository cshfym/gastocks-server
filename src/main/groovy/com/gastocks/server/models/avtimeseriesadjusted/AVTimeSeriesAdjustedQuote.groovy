package com.gastocks.server.models.avtimeseriesadjusted

import com.gastocks.server.models.IQuote
import groovy.transform.ToString

@ToString
class  AVTimeSeriesAdjustedQuote implements IQuote {

    String symbol
    List<AVTimeSeriesAdjustedDay> dailyQuoteList

}
