package com.gastocks.server.converters.quote

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.technical.MACDTechnicalData
import com.gastocks.server.models.technical.TechnicalDataWrapper
import com.gastocks.server.models.technical.TechnicalQuote
import com.gastocks.server.models.technical.TechnicalQuoteMetadata
import com.gastocks.server.models.technical.TechnicalQuoteParameters
import org.springframework.stereotype.Component

@Component
class TechnicalQuoteConverter {

    TechnicalQuote fromPersistableQuote(PersistableQuote persistableQuote, TechnicalDataWrapper technicalData) {

        new TechnicalQuote(
            quoteDate: persistableQuote.quoteDate,
            symbol: persistableQuote.symbol.identifier,
            exchangeName: persistableQuote.symbol.exchangeMarket.shortName,
            price: persistableQuote.price,
            open: persistableQuote.dayOpen,
            high: persistableQuote.dayHigh,
            low: persistableQuote.dayLow,
            volume: persistableQuote.volume,

            // Parameters
            quoteParameters: new TechnicalQuoteParameters(
                    priceChangeFromLastQuote: technicalData.quoteParameters.priceChangeFromLastQuote
            ),

            // MACD Data
            macdParameters: new MACDTechnicalData(
                emaShort: technicalData.macdTechnicalData.emaShort,
                emaLong: technicalData.macdTechnicalData.emaLong,
                macd: technicalData.macdTechnicalData.macd,
                macdSignalLine: technicalData.macdTechnicalData.macdSignalLine,
                macdHist: technicalData.macdTechnicalData.macdHist,
                centerCrossoverNegative: technicalData.macdTechnicalData.centerCrossoverNegative,
                centerCrossoverPositive: technicalData.macdTechnicalData.centerCrossoverPositive,
                signalCrossoverNegative: technicalData.macdTechnicalData.signalCrossoverNegative,
                signalCrossoverPositive: technicalData.macdTechnicalData.signalCrossoverPositive
            ),

            // Averages
            quoteMetadata: new TechnicalQuoteMetadata(
                _52WeekAverage: technicalData.quoteMetadata._52WeekAverage,
                _26WeekAverage: technicalData.quoteMetadata._26WeekAverage,
                _12WeekAverage: technicalData.quoteMetadata._12WeekAverage,
                _6WeekAverage: technicalData.quoteMetadata._6WeekAverage,
                _3WeekAverage: technicalData.quoteMetadata._3WeekAverage,
                _1WeekAverage: technicalData.quoteMetadata._1WeekAverage,

                // High/Low
                _52WeekHigh: technicalData.quoteMetadata._52WeekHigh,
                _52WeekLow: technicalData.quoteMetadata._52WeekLow,
                _26WeekHigh: technicalData.quoteMetadata._26WeekHigh,
                _26WeekLow: technicalData.quoteMetadata._26WeekLow,
                _12WeekHigh: technicalData.quoteMetadata._12WeekHigh,
                _12WeekLow: technicalData.quoteMetadata._12WeekLow,
                _6WeekHigh: technicalData.quoteMetadata._6WeekHigh,
                _6WeekLow: technicalData.quoteMetadata._6WeekLow,
                _3WeekHigh: technicalData.quoteMetadata._3WeekHigh,
                _3WeekLow: technicalData.quoteMetadata._3WeekLow,
                _1WeekHigh: technicalData.quoteMetadata._1WeekHigh,
                _1WeekLow: technicalData.quoteMetadata._1WeekLow
            )
        )
    }
}
