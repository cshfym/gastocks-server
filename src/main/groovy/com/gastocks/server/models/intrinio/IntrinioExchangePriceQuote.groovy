package com.gastocks.server.models.intrinio

import com.gastocks.server.models.IQuote

class IntrinioExchangePriceQuote implements IQuote {

    String date
    String ticker

    double open
    double high
    double low
    double close

    int volume

    double adjClose
    double adjHigh
    double adjLow
    double adjOpen
    double adjVolume

    double exDividend
    String figi
    String figiTicker
    double splitRatio

    @Override
    String toString() {
        "IntrinioExchangePriceQuote: [${ticker}] on [${date}], open [${open}], high [${high}], low [${low}], close [${close}], volume [${volume}]"
    }
}
