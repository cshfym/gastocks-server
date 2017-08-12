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
            quoteConverter.fromPersistableQuote(persistableQuote, emaShort, emaLong, emaData.emaShort, emaData.emaLong)
        }

        quotes.sort { q1, q2 -> q2.quoteDate <=> q1.quoteDate } // Descending
    }

    List<EMAData> buildEMAData(List<PersistableQuote> quoteData, int emaShortDays, int emaLongDays) {

        List<EMAData> emaData = []

        quoteData.eachWithIndex { quote, ix ->
            if (ix == 0) {
                emaData << new EMAData(quoteDate: quote.quoteDate, emaShort: quote.price, emaLong: quote.price)
            } else {
                emaData << new EMAData(
                    emaShort: calculateEMA(quote.price, emaData.get(ix - 1).emaShort, emaShortDays),
                    emaLong: calculateEMA(quote.price, emaData.get(ix - 1).emaLong, emaLongDays),
                    quoteDate: quote.quoteDate
                )
            }
        }

        emaData
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
    }

}
