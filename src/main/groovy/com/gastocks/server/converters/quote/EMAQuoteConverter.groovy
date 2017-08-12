package com.gastocks.server.converters.quote

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.quote.EMAQuote
import com.gastocks.server.models.quote.Quote
import com.gastocks.server.services.EMAQuoteService
import org.springframework.stereotype.Component

@Component
class EMAQuoteConverter {

    EMAQuote fromPersistableQuote(PersistableQuote persistableQuote, int shortParameter, int longParameter,
        double emaShort, double emaLong, double macd, double macdSignalLine) {

        new EMAQuote(
            quoteDate: persistableQuote.quoteDate,
            symbol: persistableQuote.symbol.identifier,
            exchangeName: persistableQuote.symbol.exchangeMarket.shortName,
            price: persistableQuote.price,
            open: persistableQuote.dayOpen,
            high: persistableQuote.dayHigh,
            low: persistableQuote.dayLow,
            volume: persistableQuote.volume,
            shortParameter: shortParameter,
            longParameter: longParameter,
            emaShort: emaShort,
            emaLong: emaLong,
            macd: macd,
            macdSignalLine: macdSignalLine,
            macdHist: macd - macdSignalLine
        )
    }
}
