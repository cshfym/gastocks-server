package com.gastocks.server.converters.avtimeseriesadjustedquote

import com.gastocks.server.converters.BaseConverter
import com.gastocks.server.converters.IConverter
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedDay
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedQuote
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedQuoteConstants
import com.gastocks.server.util.DateUtility
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AVTimeSeriesAdjustedQuoteConverter extends BaseConverter {

    @Override
    boolean hasData(Object obj) {
        Map timeSeriesRoot = obj."${AVTimeSeriesAdjustedQuoteConstants.TIME_SERIES_ROOT}"
        !timeSeriesRoot?.isEmpty()
    }

    /**
     * Converts an object conforming to the {@AVTimeSeriesAdjustedQuote} JSON hashmap convention
     * @param obj
     * @return
     */
    @Override
    AVTimeSeriesAdjustedQuote fromObject(Object obj) {

        def metadataRoot = obj."${AVTimeSeriesAdjustedQuoteConstants.METADATA_ROOT}"
        def timeSeriesRoot = obj."${AVTimeSeriesAdjustedQuoteConstants.TIME_SERIES_ROOT}"

        def quote = new AVTimeSeriesAdjustedQuote(
                symbol: metadataRoot."${AVTimeSeriesAdjustedQuoteConstants.META_SYMBOL}",
                dailyQuoteList: []
        )

        timeSeriesRoot.each { day, attributes ->
            def adjustedDay = new AVTimeSeriesAdjustedDay()
            adjustedDay.with {
                date = DateTime.parse(day as String)
                dayOpen = parseToDouble(attributes."${AVTimeSeriesAdjustedQuoteConstants.TS_OPEN}" as String)
                dayHigh = parseToDouble(attributes."${AVTimeSeriesAdjustedQuoteConstants.TS_HIGH}" as String)
                dayLow =  parseToDouble(attributes."${AVTimeSeriesAdjustedQuoteConstants.TS_LOW}" as String)
                close =  parseToDouble(attributes."${AVTimeSeriesAdjustedQuoteConstants.TS_CLOSE}" as String)
                adjustedClose = parseToDouble(attributes."${AVTimeSeriesAdjustedQuoteConstants.TS_ADJUSTED_CLOSE}" as String)
                volume = parseToInteger(attributes."${AVTimeSeriesAdjustedQuoteConstants.TS_VOLUME}" as String)
                dividend = parseToDouble(attributes."${AVTimeSeriesAdjustedQuoteConstants.TS_DIVIDEND_AMOUNT}" as String)
                splitCoefficient = parseToDouble(attributes."${AVTimeSeriesAdjustedQuoteConstants.TS_SPLIT_COEFFICIENT}" as String)
            }
            quote.dailyQuoteList << adjustedDay
        }

        quote
    }

}
