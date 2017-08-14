package com.gastocks.server.services

import com.gastocks.server.converters.quote.TechnicalQuoteConverter
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.technical.MACDTechnicalData
import com.gastocks.server.models.technical.TechnicalDataWrapper
import com.gastocks.server.models.technical.TechnicalQuote
import com.gastocks.server.models.simulation.MACDRequestParameters
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Slf4j
@Service
class TechnicalQuoteService {

    @Autowired
    QuotePersistenceService quotePersistenceService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    TechnicalQuoteConverter quoteConverter

    @Autowired
    MACDService macdService

    @Autowired
    TechnicalToolsService technicalToolsService

    /**
     * Retrieve all technical quotes for a given symbol identifier
     * Implicitly ordered by quote date descending
     * @param identifier
     * @return List<Quote>
     */
    @Cacheable(value = "getTechnicalQuotesForSymbol")
    List<TechnicalQuote> getTechnicalQuotesForSymbol(String identifier, MACDRequestParameters macdParameters) {

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)

        if (!symbol) { throw new QuoteNotFoundException(identifier: identifier) }

        List<PersistableQuote> persistableQuotes = quotePersistenceService.findAllQuotesForSymbol(symbol)

        persistableQuotes.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate } // Ascending

        // Calculate technical data points for all quotes available.
        List<TechnicalDataWrapper> technicalDataList = buildTechnicalData(persistableQuotes, macdParameters.macdShortPeriod, macdParameters.macdLongPeriod)

        // Return sorted collection of TechnicalQuote objects
        def quotes = persistableQuotes.collect { persistableQuote ->
            def technicalData = technicalDataList.find { it.quoteDate == persistableQuote.quoteDate }
            quoteConverter.fromPersistableQuote(persistableQuote, technicalData, macdParameters.macdShortPeriod, macdParameters.macdLongPeriod)
        }

        quotes.sort { q1, q2 -> q2.quoteDate <=> q1.quoteDate } // Descending
    }

    protected List<TechnicalDataWrapper> buildTechnicalData(List<PersistableQuote> quoteData, int emaShortDays, int emaLongDays) {

        List<TechnicalDataWrapper> technicalDataList = []

        quoteData.eachWithIndex { quote, ix ->
            if (ix == 0) {
                def macdTechnicalData = new MACDTechnicalData(emaShort: quote.price, emaLong: quote.price)
                technicalDataList << new TechnicalDataWrapper(quoteDate: quote.quoteDate, macdTechnicalData: macdTechnicalData)
            } else {
                double emaShort = technicalToolsService.calculateEMA(quote.price, technicalDataList.get(ix - 1).macdTechnicalData.emaShort, emaShortDays)
                double emaLong = technicalToolsService.calculateEMA(quote.price, technicalDataList.get(ix - 1).macdTechnicalData.emaLong, emaLongDays)
                double macd = (emaShort - emaLong).round(4)
                technicalDataList << new TechnicalDataWrapper(
                        macdTechnicalData: new MACDTechnicalData(
                            emaShort: emaShort,
                            emaLong: emaLong,
                            macd: macd),
                        quoteDate: quote.quoteDate)
            }
        }

        macdService.buildMACDSignalData(technicalDataList)

        technicalDataList
    }

}
