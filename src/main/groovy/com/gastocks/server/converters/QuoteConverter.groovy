package com.gastocks.server.converters

import com.gastocks.server.models.Quote
import com.gastocks.server.models.RGSQConstants
import org.joda.time.DateTime
import org.springframework.data.jpa.domain.AbstractAuditable_

class QuoteConverter {

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

        quote.with {
            symbol = objRoot."${RGSQConstants.SYMBOL}"
            exchangeName = objRoot."${RGSQConstants.EXCHANGE_NAME}"
            latestPrice = parseToDouble(objRoot."${RGSQConstants.LATEST_PRICE}" as String)
            currentTradingDayOpen = parseToDouble(objRoot."${RGSQConstants.CURRENT_TRADING_DAY_OPEN}" as String)
            currentTradingDayHigh = parseToDouble(objRoot."${RGSQConstants.CURRENT_TRADING_DAY_HIGH}" as String)
            currentTradingDayLow = parseToDouble(objRoot."${RGSQConstants.CURRENT_TRADING_DAY_LOW}" as String)
            previousTradingDayClose = parseToDouble( objRoot."${RGSQConstants.PREVIOUS_TRADING_DAY_CLOSE}" as String)
            priceChange = parseToDouble(objRoot."${RGSQConstants.PRICE_CHANGE}" as String)
            // priceChangePercentage = objRoot."${RGSQConstants.PRICE_CHANGE_PERCENTAGE}"
            volume = parseToInteger(objRoot."${RGSQConstants.VOLUME}" as String)
            //lastUpdated = objRoot."${RGSQConstants.LAST_UPDATED_DATE}"
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

    static Integer parseToInteger(String val) {
        try {
            Integer.parseInt(val)
        } catch (Exception ex) {
            null
        }
    }
}
