package com.gastocks.server.converters.avglobalquote

import com.gastocks.server.converters.ConverterUtils
import com.gastocks.server.models.avglobalquote.AVGlobalQuote
import com.gastocks.server.models.avglobalquote.AVGlobalQuoteConstants
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class AVGlobalQuoteConverter extends ConverterUtils {

    final static DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("MMM d, K:mma zzz") // i.e. Jul 6, 4:04PM EDT

    static boolean hasData(Object obj) {
        Map objRoot = obj."${AVGlobalQuoteConstants.MAP_ROOT}"
        !objRoot.isEmpty()
    }

    /**
     * Accepts an object conforming to the AVGlobalQuote JSON hashmap convention
     * @param obj
     * @return
     */
    static AVGlobalQuote fromAVGlobalQuote(Object obj) {

        def quote = new AVGlobalQuote()

        def objRoot = obj."${AVGlobalQuoteConstants.MAP_ROOT}"

        DateTime parsedDateTime = DATE_FORMAT.parseDateTime(objRoot."${AVGlobalQuoteConstants.LAST_UPDATED_DATE}" as String)
            .withYear(new DateTime().year)

        quote.with {
            symbol = objRoot."${AVGlobalQuoteConstants.SYMBOL}"
            exchangeName = objRoot."${AVGlobalQuoteConstants.EXCHANGE_NAME}"
            latestPrice = parseToDouble(objRoot."${AVGlobalQuoteConstants.LATEST_PRICE}" as String)
            currentTradingDayOpen = parseToDouble(objRoot."${AVGlobalQuoteConstants.CURRENT_TRADING_DAY_OPEN}" as String)
            currentTradingDayHigh = parseToDouble(objRoot."${AVGlobalQuoteConstants.CURRENT_TRADING_DAY_HIGH}" as String)
            currentTradingDayLow = parseToDouble(objRoot."${AVGlobalQuoteConstants.CURRENT_TRADING_DAY_LOW}" as String)
            previousTradingDayClose = parseToDouble( objRoot."${AVGlobalQuoteConstants.PREVIOUS_TRADING_DAY_CLOSE}" as String)
            priceChange = parseToDouble(objRoot."${AVGlobalQuoteConstants.PRICE_CHANGE}" as String)
            priceChangePercentage = parseToFloat(objRoot."${AVGlobalQuoteConstants.PRICE_CHANGE_PERCENTAGE}".replace("%","") as String)
            volume = parseToInteger(objRoot."${AVGlobalQuoteConstants.VOLUME}" as String)
            lastUpdated = parsedDateTime
        }

        quote
    }

}
