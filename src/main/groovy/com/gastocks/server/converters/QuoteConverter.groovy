package com.gastocks.server.converters

import com.gastocks.server.models.Quote
import com.gastocks.server.models.RGSQConstants
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.data.jpa.domain.AbstractAuditable_

class QuoteConverter {

    final static DateTimeFormatter dateFormat = DateTimeFormat.forPattern("MMM d, K:mma 'EDT'") // i.e. Jul 6, 4:04PM EDT

    static boolean hasData(Object obj) {
        Map objRoot = obj."${RGSQConstants.MAP_ROOT}"
        !objRoot.isEmpty()
    }

    /**
     * Accepts an object conforming to the RGSQConstants hashmap convention
     * @param obj
     * @return
     */
    static Quote from(Object obj) {

        def quote = new Quote()

        def objRoot = obj."${RGSQConstants.MAP_ROOT}"

        DateTime parsedDateTime = dateFormat.parseDateTime(objRoot."${RGSQConstants.LAST_UPDATED_DATE}" as String)
            .withZone(DateTimeZone.forID("America/New_York"))
            .withYear(new DateTime().year)

        quote.with {
            symbol = objRoot."${RGSQConstants.SYMBOL}"
            exchangeName = objRoot."${RGSQConstants.EXCHANGE_NAME}"
            latestPrice = parseToDouble(objRoot."${RGSQConstants.LATEST_PRICE}" as String)
            currentTradingDayOpen = parseToDouble(objRoot."${RGSQConstants.CURRENT_TRADING_DAY_OPEN}" as String)
            currentTradingDayHigh = parseToDouble(objRoot."${RGSQConstants.CURRENT_TRADING_DAY_HIGH}" as String)
            currentTradingDayLow = parseToDouble(objRoot."${RGSQConstants.CURRENT_TRADING_DAY_LOW}" as String)
            previousTradingDayClose = parseToDouble( objRoot."${RGSQConstants.PREVIOUS_TRADING_DAY_CLOSE}" as String)
            priceChange = parseToDouble(objRoot."${RGSQConstants.PRICE_CHANGE}" as String)
            priceChangePercentage = parseToFloat(objRoot."${RGSQConstants.PRICE_CHANGE_PERCENTAGE}".replace("%","") as String)
            volume = parseToInteger(objRoot."${RGSQConstants.VOLUME}" as String)
            lastUpdated = parsedDateTime
        }

        quote
    }

    static Double parseToDouble(String val) {
        try {
            Double.parseDouble(val)
        } catch (Exception ex) {
            null
        }
    }

    static Double parseToFloat(String val) {
        try {
            Float.parseFloat(val)
        } catch (Exception ex) {
            null
        }
    }

    static Integer parseToInteger(String val) {
        try {
            Integer.parseInt(val)
        } catch (Exception ex) {
            null
        }
    }
}
