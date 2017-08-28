package com.gastocks.server.services

import com.gastocks.server.converters.quote.TechnicalQuoteConverter
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.simulation.SimulationRequest
import com.gastocks.server.models.technical.TechnicalDataWrapper
import com.gastocks.server.models.technical.TechnicalQuote
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
    List<TechnicalQuote> getTechnicalQuotesForSymbol(String identifier, SimulationRequest request) {

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)

        if (!symbol) { throw new QuoteNotFoundException(identifier: identifier) }

        List<PersistableQuote> persistableQuotes = quotePersistenceService.findAllQuotesForSymbol(symbol)

        persistableQuotes.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate } // Ascending

        // Calculate technical data points for all quotes available.
        List<TechnicalDataWrapper> technicalDataList = buildTechnicalData(persistableQuotes, request.macdParameters.macdShortPeriod,
            request.macdParameters.macdLongPeriod)

        // Return sorted collection of TechnicalQuote objects
        def quotes = persistableQuotes.collect { persistableQuote ->
            def technicalData = technicalDataList.find { it.quoteDate == persistableQuote.quoteDate }
            quoteConverter.fromPersistableQuote(persistableQuote, technicalData, request.macdParameters.macdShortPeriod, request.macdParameters.macdLongPeriod)
        }

        quotes.sort { q1, q2 -> q2.quoteDate <=> q1.quoteDate } // Descending
    }

    protected List<TechnicalDataWrapper> buildTechnicalData(List<PersistableQuote> quoteData, int emaShortDays, int emaLongDays) {

        List<TechnicalDataWrapper> technicalDataList = []

        // Fill technicalDataList 1:1 for each quote
        quoteData.each { quote ->
            def wrapper = new TechnicalDataWrapper(quoteDate: quote.quoteDate)
            calculateAveragesAndHighLows(quote, quoteData, wrapper)
            technicalDataList << wrapper
        }

        macdService.buildMACDTechnicalData(technicalDataList, quoteData, emaShortDays, emaLongDays)
        macdService.buildMACDSignalData(technicalDataList)

        technicalDataList
    }

    /**
     * Calculates weekly averages for varying periods starting with the quote, working backward.
     * @param quote
     * @param quoteData
     */
    protected void calculateAveragesAndHighLows(PersistableQuote quote, List<PersistableQuote> quoteData, TechnicalDataWrapper wrapper) {

        // Capture quote date from input quote, iterate backward
        List<PersistableQuote> relevantQuotes = quoteData.findAll { it.quoteDate <= quote.quoteDate }

        relevantQuotes.sort { q1, q2 -> q2.quoteDate <=> q1.quoteDate }

        def m52WeekQuotes = relevantQuotes.findAll { it.quoteDate >= (quote.quoteDate - 364) }
        def m26WeekQuotes = relevantQuotes.findAll { it.quoteDate >= (quote.quoteDate - 182) }
        def m12WeekQuotes = relevantQuotes.findAll { it.quoteDate >= (quote.quoteDate - 84) }
        def m6WeekQuotes = relevantQuotes.findAll { it.quoteDate >= (quote.quoteDate - 42) }
        def m3WeekQuotes = relevantQuotes.findAll { it.quoteDate >= (quote.quoteDate - 21) }
        def m1WeekQuotes = relevantQuotes.findAll { it.quoteDate >= (quote.quoteDate - 7) }

        wrapper._52WeekAverage = (m52WeekQuotes.size() > 0) ? (m52WeekQuotes*.price.sum() / m52WeekQuotes.size()).round(2) : 0
        wrapper._26WeekAverage = (m26WeekQuotes.size() > 0) ? (m26WeekQuotes*.price.sum() / m26WeekQuotes.size()).round(2) : 0
        wrapper._12WeekAverage = (m12WeekQuotes.size() > 0) ? (m12WeekQuotes*.price.sum() / m12WeekQuotes.size()).round(2) : 0
        wrapper._6WeekAverage = (m6WeekQuotes.size() > 0) ? (m6WeekQuotes*.price.sum() / m6WeekQuotes.size()).round(2) : 0
        wrapper._3WeekAverage = (m3WeekQuotes.size() > 0) ? (m3WeekQuotes*.price.sum() / m3WeekQuotes.size()).round(2) : 0
        wrapper._1WeekAverage = (m1WeekQuotes.size() > 0) ? (m1WeekQuotes*.price.sum() / m1WeekQuotes.size()).round(2) : 0

        wrapper._52WeekHigh = m52WeekQuotes.max { it.price }.price
        wrapper._52WeekLow = m52WeekQuotes.min { it.price }.price
        wrapper._26WeekHigh = m26WeekQuotes.max { it.price }.price
        wrapper._26WeekLow = m26WeekQuotes.min { it.price }.price
        wrapper._12WeekHigh = m12WeekQuotes.max { it.price }.price
        wrapper._12WeekLow = m12WeekQuotes.min { it.price }.price
        wrapper._6WeekHigh = m6WeekQuotes.max { it.price }.price
        wrapper._6WeekLow = m6WeekQuotes.min { it.price }.price
        wrapper._3WeekHigh = m3WeekQuotes.max { it.price }.price
        wrapper._3WeekLow = m3WeekQuotes.min { it.price }.price
        wrapper._1WeekHigh = m1WeekQuotes.max { it.price }.price
        wrapper._1WeekLow = m1WeekQuotes.min { it.price }.price

    }

}
