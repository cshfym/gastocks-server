package com.gastocks.server.services.technical

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.converters.quote.TechnicalQuoteConverter
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.technical.TechnicalDataWrapper
import com.gastocks.server.models.technical.request.TechnicalQuoteRequestParameters
import com.gastocks.server.models.technical.response.TechnicalQuote
import com.gastocks.server.models.technical.response.TechnicalQuoteMetadata
import com.gastocks.server.models.technical.response.TechnicalQuoteParameters
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
    TechnicalQuoteConverter technicalQuoteConverter

    @Autowired
    MACDService macdService

    @Autowired
    RSIService rsiService

    /**
     * Retrieve all technical quotes for a given symbol identifier
     * Implicitly ordered by quote date descending
     * @param identifier
     * @return List<Quote>
     */
    @Cacheable(value = CacheConfiguration.GET_TECHNICAL_QUOTES_FOR_SYMBOL)
    List<TechnicalQuote> getTechnicalQuotesForSymbol(String identifier, TechnicalQuoteRequestParameters parameters) {

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)

        if (!symbol) { throw new QuoteNotFoundException(identifier: identifier) }

        List<PersistableQuote> persistableQuotes = quotePersistenceService.findAllQuotesForSymbol(symbol)

        persistableQuotes.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate } // Ascending

        // Calculate technical data points for all quotes available.
        List<TechnicalDataWrapper> technicalDataList = buildTechnicalData(persistableQuotes, parameters)

        // Return sorted collection of TechnicalQuote objects
        def quotes = persistableQuotes.collect { persistableQuote ->
            def technicalData = technicalDataList.find { it.quoteDate == persistableQuote.quoteDate }
            technicalQuoteConverter.fromPersistableQuote(persistableQuote, technicalData)
        }

        quotes.sort { q1, q2 -> q2.quoteDate <=> q1.quoteDate } // Descending
    }

    protected List<TechnicalDataWrapper> buildTechnicalData(List<PersistableQuote> quoteData, TechnicalQuoteRequestParameters parameters) {

        List<TechnicalDataWrapper> technicalDataWrapperList = []

        // Fill technicalDataWrapperList 1:1 for each quote
        quoteData.eachWithIndex { quote, ix ->
            def wrapper = new TechnicalDataWrapper(
                    quoteDate: quote.quoteDate,
                    price: quote.price,
                    quoteParameters: new TechnicalQuoteParameters(priceChangeFromLastQuote: false)
            )
            calculateAveragesAndHighLows(quote, quoteData, wrapper)
            if ((ix > 0) && (quoteData[ix - 1].price != quote.price)) { wrapper.quoteParameters.priceChangeFromLastQuote = true }
            technicalDataWrapperList << wrapper
        }

        // MACD
        macdService.buildMACDTechnicalData(technicalDataWrapperList, quoteData, parameters.macdRequestParameters)
        macdService.buildMACDSignalData(technicalDataWrapperList)

        // RSI
        rsiService.buildRSITechnicalData(technicalDataWrapperList, quoteData, parameters.rsiRequestParameters)
        rsiService.buildRSISignalData(technicalDataWrapperList)

        technicalDataWrapperList
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

        wrapper.quoteMetadata = new TechnicalQuoteMetadata()
        wrapper.quoteMetadata.with {
            _52WeekAverage = (m52WeekQuotes.size() > 0) ? (m52WeekQuotes*.price.sum() / m52WeekQuotes.size()).round(2) : 0
            _26WeekAverage = (m26WeekQuotes.size() > 0) ? (m26WeekQuotes*.price.sum() / m26WeekQuotes.size()).round(2) : 0
            _12WeekAverage = (m12WeekQuotes.size() > 0) ? (m12WeekQuotes*.price.sum() / m12WeekQuotes.size()).round(2) : 0
            _6WeekAverage = (m6WeekQuotes.size() > 0) ? (m6WeekQuotes*.price.sum() / m6WeekQuotes.size()).round(2) : 0
            _3WeekAverage = (m3WeekQuotes.size() > 0) ? (m3WeekQuotes*.price.sum() / m3WeekQuotes.size()).round(2) : 0
            _1WeekAverage = (m1WeekQuotes.size() > 0) ? (m1WeekQuotes*.price.sum() / m1WeekQuotes.size()).round(2) : 0
            _52WeekHigh = m52WeekQuotes.max { it.price }.price
            _52WeekLow = m52WeekQuotes.min { it.price }.price
            _26WeekHigh = m26WeekQuotes.max { it.price }.price
            _26WeekLow = m26WeekQuotes.min { it.price }.price
            _12WeekHigh = m12WeekQuotes.max { it.price }.price
            _12WeekLow = m12WeekQuotes.min { it.price }.price
            _6WeekHigh = m6WeekQuotes.max { it.price }.price
            _6WeekLow = m6WeekQuotes.min { it.price }.price
            _3WeekHigh = m3WeekQuotes.max { it.price }.price
            _3WeekLow = m3WeekQuotes.min { it.price }.price
            _1WeekHigh = m1WeekQuotes.max { it.price }.price
            _1WeekLow = m1WeekQuotes.min { it.price }.price
        }
    }

}
