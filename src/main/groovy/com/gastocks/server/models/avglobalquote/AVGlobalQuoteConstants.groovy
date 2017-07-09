package com.gastocks.server.models.avglobalquote

/**
 * Realtime Global Securities Quote - JSON HashMap Constants
 * [
 *   Realtime Global Securities Quote:[
 *     01. Symbol:MYGN,
 *     02. Exchange Name:NASDAQ,
 *     03. Latest Price:25.7200,
 *     04. Open (Current Trading Day):25.8300,
 *     05. High (Current Trading Day):26.1700,
 *     06. Low (Current Trading Day):25.6500,
 *     07. Close (Previous Trading Day):25.8400,
 *     08. Price Change:-0.1200,
 *     09. Price Change Percentage:-0.46%,
 *     10. Volume (Current Trading Day):201703,
 *     11. Last Updated:Jul 3, 1:00PM EDT
 *   ]
 * ]
 **/

class AVGlobalQuoteConstants {

    static final String AV_GLOBAL_QUOTE_URI = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="

    static final String MAP_ROOT = "Realtime Global Securities Quote"

    static final String SYMBOL = "01. Symbol"
    static final String EXCHANGE_NAME = "02. Exchange Name"
    static final String LATEST_PRICE = "03. Latest Price" // Double
    static final String CURRENT_TRADING_DAY_OPEN = "04. Open (Current Trading Day)" // Double
    static final String CURRENT_TRADING_DAY_HIGH = "05. High (Current Trading Day)" // Double
    static final String CURRENT_TRADING_DAY_LOW = "06. Low (Current Trading Day)" // Double
    static final String PREVIOUS_TRADING_DAY_CLOSE = "07. Close (Previous Trading Day)" // Double
    static final String PRICE_CHANGE = "08. Price Change" // String %, i.e. "-0.46%"
    static final String PRICE_CHANGE_PERCENTAGE = "09. Price Change Percentage"
    static final String VOLUME = "10. Volume (Current Trading Day)" // Integer?
    static final String LAST_UPDATED_DATE = "11. Last Updated" // MMM dd, HH:MMA/P Z: "Jul 3, 1:00PM EDT"

}
