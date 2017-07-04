package com.gastocks.server.converters

import com.gastocks.server.models.Quote
import com.gastocks.server.models.RGSQConstants
import org.joda.time.DateTime

class QuoteConverter {

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
            latestPrice = Double.parseDouble(objRoot."${RGSQConstants.LATEST_PRICE}" as String)
            currentTradingDayOpen = Double.parseDouble(objRoot."${RGSQConstants.CURRENT_TRADING_DAY_OPEN}" as String)
            currentTradingDayHigh = Double.parseDouble(objRoot."${RGSQConstants.CURRENT_TRADING_DAY_HIGH}" as String)
            currentTradingDayLow = Double.parseDouble(objRoot."${RGSQConstants.CURRENT_TRADING_DAY_LOW}" as String)
            previousTradingDayClose =Double.parseDouble( objRoot."${RGSQConstants.PREVIOUS_TRADING_DAY_CLOSE}" as String)
            priceChange = Double.parseDouble(objRoot."${RGSQConstants.PRICE_CHANGE}" as String)
            // priceChangePercentage = objRoot."${RGSQConstants.PRICE_CHANGE_PERCENTAGE}"
            volume = Integer.parseInt(objRoot."${RGSQConstants.VOLUME}" as String)
            //lastUpdated = objRoot."${RGSQConstants.LAST_UPDATED_DATE}"
        }

        quote
    }
}
