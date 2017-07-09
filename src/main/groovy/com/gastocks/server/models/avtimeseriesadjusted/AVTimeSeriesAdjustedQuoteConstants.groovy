package com.gastocks.server.models.avtimeseriesadjusted

/**
  Daily Time Series Securities Quote - JSON HashMap Constants
 {
     "Meta Data": {
         "1. Information": "Daily Time Series with Splits and Dividend Events",
         "2. Symbol": "MSFT",
         "3. Last Refreshed": "2017-07-07",
         "4. Output Size": "Compact",
         "5. Time Zone": "US/Eastern"
     },
     "Time Series (Daily)": {
         "2017-07-07": {
             "1. open": "68.7000",
             "2. high": "69.8400",
             "3. low": "68.7000",
             "4. close": "69.4600",
             "5. adjusted close": "69.4600",
             "6. volume": "15897154",
             "7. dividend amount": "0.00",
             "8. split coefficient": "1.0000"
         },
         "2017-07-07": {
             "1. open": "68.7000",
             "2. high": "69.8400",
             "3. low": "68.7000",
             "4. close": "69.4600",
             "5. adjusted close": "69.4600",
             "6. volume": "15897154",
             "7. dividend amount": "0.00",
             "8. split coefficient": "1.0000"
         },
         ...
      }
 }
 **/

class AVTimeSeriesAdjustedQuoteConstants {

    // URI for "Compact" - 5-6 months of history
    static final String AV_TS_ADJ_QUOTE_URI = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="

    // URI for "Full" output - all history available
    static final String AV_TS_ADJ_QUOTE_FULL_URI = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&outputsize=full&symbol="

    static final String METADATA_ROOT = "Meta Data"
    static final String META_INFORMATION = "1. Information"
    static final String META_SYMBOL = "2. Symbol"
    static final String META_LAST_REFRESHED = "3. Last Refreshed"
    static final String META_OUTPUT_SIZE = "4. Output Size"
    static final String META_TIME_ZONE = "5. Time Zone"

    static final String TIME_SERIES_ROOT = "Time Series (Daily)"

    static final String TS_OPEN = "1. open"
    static final String TS_HIGH = "2. high"
    static final String TS_LOW = "3. low"
    static final String TS_CLOSE = "4. close"
    static final String TS_ADJUSTED_CLOSE = "5. adjusted close"
    static final String TS_VOLUME = "6. volume"
    static final String TS_DIVIDEND_AMOUNT = "7. dividend amount"
    static final String TS_SPLIT_COEFFICIENT = "8. split coefficient"

}
