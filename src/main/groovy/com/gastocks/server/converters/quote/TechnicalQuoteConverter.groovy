package com.gastocks.server.converters.quote

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.technical.TechnicalDataWrapper
import com.gastocks.server.models.technical.TechnicalQuote
import com.gastocks.server.services.TechnicalQuoteService
import org.springframework.stereotype.Component

@Component
class TechnicalQuoteConverter {

    TechnicalQuote fromPersistableQuote(PersistableQuote persistableQuote, TechnicalDataWrapper technicalData, int shortParameter, int longParameter) {

        new TechnicalQuote(
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

            // MACD Data
            emaShort: technicalData.macdTechnicalData.emaShort,
            emaLong: technicalData.macdTechnicalData.emaLong,
            macd: technicalData.macdTechnicalData.macd,
            macdSignalLine: technicalData.macdTechnicalData.macdSignalLine,
            macdHist: technicalData.macdTechnicalData.macdHist,
            centerCrossoverNegative: technicalData.macdTechnicalData.centerCrossoverNegative,
            centerCrossoverPositive: technicalData.macdTechnicalData.centerCrossoverPositive,
            signalCrossoverNegative: technicalData.macdTechnicalData.signalCrossoverNegative,
            signalCrossoverPositive: technicalData.macdTechnicalData.signalCrossoverPositive,

            // Averages
            _52WeekAverage: technicalData._52WeekAverage,
            _26WeekAverage: technicalData._26WeekAverage,
            _12WeekAverage: technicalData._12WeekAverage,
            _6WeekAverage: technicalData._6WeekAverage,
            _3WeekAverage: technicalData._3WeekAverage,
            _1WeekAverage: technicalData._1WeekAverage
        )
    }
}
