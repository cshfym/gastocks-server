package com.gastocks.server.converters.quote

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.quote.EMAQuote
import com.gastocks.server.models.quote.Quote
import com.gastocks.server.services.EMAQuoteService
import org.springframework.stereotype.Component

@Component
class EMAQuoteConverter {

    EMAQuote fromPersistableQuote(PersistableQuote persistableQuote, EMAQuoteService.EMAData emaData, int shortParameter, int longParameter) {

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
            emaShort: emaData.emaShort,
            emaLong: emaData.emaLong,
            macd: emaData.macd,
            macdSignalLine: emaData.macdSignalLine,
            macdHist: emaData.macdHist,
            centerCrossoverNegative: emaData.centerCrossoverNegative,
            centerCrossoverPositive: emaData.centerCrossoverPositive,
            signalCrossoverNegative: emaData.signalCrossoverNegative,
            signalCrossoverPositive: emaData.signalCrossoverPositive
        )
    }
}
