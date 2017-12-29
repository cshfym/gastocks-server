package com.gastocks.server.converters.quote

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.quote.Quote
import org.springframework.stereotype.Component

@Component
class QuoteConverter {

    static Quote fromPersistableQuote(PersistableQuote persistableQuote) {

        new Quote(
            quoteDate: persistableQuote.quoteDate,
            symbol: persistableQuote.symbol.identifier,
            exchangeName: persistableQuote.symbol.exchangeMarket.shortName,
            price: persistableQuote.price,
            open: persistableQuote.dayOpen,
            high: persistableQuote.dayHigh,
            low: persistableQuote.dayLow,
            volume: persistableQuote.volume
        )
    }
}
