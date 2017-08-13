package com.gastocks.server.services

import com.gastocks.server.converters.quote.EMAQuoteConverter
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.quote.EMAQuote
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Slf4j
@Service
class EMAQuoteService {

    @Autowired
    QuotePersistenceService quotePersistenceService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    EMAQuoteConverter quoteConverter

    /**
     * Retrieve all quotes for a given symbol identifier
     * Implicitly ordered by quote date descending
     * @param identifier
     * @return List<Quote>
     */
    @Cacheable(value = "getEMAQuotesForSymbol")
    List<EMAQuote> getEMAQuotesForSymbol(String identifier, int emaShort, int emaLong) {

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)

        if (!symbol) {
          throw new QuoteNotFoundException(identifier: identifier)
        }

        List<PersistableQuote> persistableQuotes = quotePersistenceService.findAllQuotesForSymbol(symbol)

        persistableQuotes.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate } // Ascending

        // Calculate EMA data
        List<EMAData> emaDataList = buildEMAData(persistableQuotes, emaShort, emaLong)

        // Return sorted collection of EMAQuote objects
        def quotes = persistableQuotes.collect { persistableQuote ->
            def emaData = emaDataList.find { it.quoteDate == persistableQuote.quoteDate }
            quoteConverter.fromPersistableQuote(persistableQuote, emaData, emaShort, emaLong)
        }

        quotes.sort { q1, q2 -> q2.quoteDate <=> q1.quoteDate } // Descending
    }

    List<EMAData> buildEMAData(List<PersistableQuote> quoteData, int emaShortDays, int emaLongDays) {

        List<EMAData> emaDataList = []

        quoteData.eachWithIndex { quote, ix ->
            if (ix == 0) {
                emaDataList << new EMAData(quoteDate: quote.quoteDate, emaShort: quote.price, emaLong: quote.price)
            } else {
                double emaShort = calculateEMA(quote.price, emaDataList.get(ix - 1).emaShort, emaShortDays)
                double emaLong = calculateEMA(quote.price, emaDataList.get(ix - 1).emaLong, emaLongDays)
                double macd = (emaShort - emaLong).round(4)
                emaDataList << new EMAData(emaShort: emaShort, emaLong: emaLong, quoteDate: quote.quoteDate, macd: macd)
            }
        }

        buildMACDSignalData(emaDataList)

        emaDataList
    }

    /**
     * The MACD signal line is the 9-day EMA of the MACD.
     * @param emaDataList
     */
    void buildMACDSignalData(List<EMAData> emaDataList) {

        emaDataList.eachWithIndex { emaData, ix ->
            if (ix == 0) {
                emaDataList[ix].macdSignalLine = emaData.macd
                emaDataList[ix].macdHist = (emaData.macd - emaDataList[ix].macdSignalLine).round(4)
            } else {
                emaDataList[ix].macdSignalLine = calculateEMA(emaData.macd, emaDataList.get(ix - 1).macdSignalLine, 9)
                emaDataList[ix].macdHist = (emaData.macd - emaDataList[ix].macdSignalLine).round(4)

                // Positive MACD center line crossover
                if ((emaDataList[ix].macd >= 0.0) && (emaDataList[ix - 1].macd < 0.0 )) {
                    emaDataList[ix].centerCrossoverPositive = true
                    emaDataList[ix].centerCrossoverNegative = false
                }
                // Negative MACD center line crossover
                if ((emaDataList[ix].macd < 0.0) && (emaDataList[ix - 1].macd >= 0.0 )) {
                    emaDataList[ix].centerCrossoverNegative = true
                    emaDataList[ix].centerCrossoverPositive = false
                }
                // Positive MACD signal line crossover
                if ((emaDataList[ix].macd >= emaDataList[ix].macdSignalLine) &&
                    (emaDataList[ix - 1].macd < emaDataList[ix - 1].macdSignalLine)) {
                    emaDataList[ix].signalCrossoverPositive = true
                    emaDataList[ix].signalCrossoverNegative = false
                }
                // Positive MACD signal line crossover
                if ((emaDataList[ix].macd < emaDataList[ix].macdSignalLine) &&
                        (emaDataList[ix - 1].macd >= emaDataList[ix - 1].macdSignalLine)) {
                    emaDataList[ix].signalCrossoverNegative = true
                    emaDataList[ix].signalCrossoverPositive = false
                }
            }
        }
    }

    /**
     * Calculates the exponential moving average for the given price, previous EMA, and days
     * EMA = (Current price - EMA(previous day)) x multiplier + EMA(previous day)
     **/
    double calculateEMA(double currentPrice, double previousDayEMA, int days) {
        double multiplier = 2/(+days + 1)
        double exponentialMovingAverage = (currentPrice * multiplier) + (previousDayEMA * (1 - multiplier))
        exponentialMovingAverage.round(4)
    }

    class EMAData {
        Date quoteDate
        double emaShort
        double emaLong
        double macd
        double macdSignalLine
        double macdHist
        boolean centerCrossoverPositive
        boolean centerCrossoverNegative
        boolean signalCrossoverPositive
        boolean signalCrossoverNegative
    }

}
