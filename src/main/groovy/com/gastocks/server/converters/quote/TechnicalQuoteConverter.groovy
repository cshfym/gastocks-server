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
            priceChangeFromLastQuote: technicalData.priceChangeFromLastQuote,

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
            _1WeekAverage: technicalData._1WeekAverage,

            // High/Low
            _52WeekHigh: technicalData._52WeekHigh,
            _52WeekLow: technicalData._52WeekLow,
            _26WeekHigh: technicalData._26WeekHigh,
            _26WeekLow: technicalData._26WeekLow,
            _12WeekHigh: technicalData._12WeekHigh,
            _12WeekLow: technicalData._12WeekLow,
            _6WeekHigh: technicalData._6WeekHigh,
            _6WeekLow: technicalData._6WeekLow,
            _3WeekHigh: technicalData._3WeekHigh,
            _3WeekLow: technicalData._3WeekLow,
            _1WeekHigh: technicalData._1WeekHigh,
            _1WeekLow: technicalData._1WeekLow

        )
    }
}
