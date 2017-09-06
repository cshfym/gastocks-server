package com.gastocks.server.services

import com.gastocks.server.converters.symbol.SymbolConverter
import com.gastocks.server.jms.sender.SymbolExtendedQueueSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.PersistableSymbolExtended
import com.gastocks.server.models.domain.ViewSymbolExtended
import com.gastocks.server.models.symbol.Symbol
import com.gastocks.server.services.domain.SymbolExtendedPersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import com.gastocks.server.services.domain.ViewSymbolExtendedPersistenceService
import com.gastocks.server.util.StatisticsUtility
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
class SymbolService {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    QuoteService quoteService

    @Autowired
    SymbolConverter symbolConverter

    @Autowired
    SymbolExtendedQueueSender symbolExtendedQueueSender

    @Autowired
    SymbolExtendedPersistenceService symbolExtendedPersistenceService

    @Autowired
    ViewSymbolExtendedPersistenceService viewSymbolExtendedPersistenceService

    @Autowired
    StatisticsUtility statisticsUtility

    /**
     * Finds all symbols available
     * @return List<Symbol>
     */
    List<Symbol> findAllSymbols() {

        def startStopwatch = System.currentTimeMillis()

        List<PersistableSymbol> allSymbols = symbolPersistenceService.findAllSymbols()

        def symbols = allSymbols.collect { persistableSymbol ->
            symbolConverter.fromPersistableSymbol(persistableSymbol)
        }.sort { q1, q2 -> q1.identifier <=> q2.identifier } // Ascending by identifier, i.e. MYGN

        log.info "Method findAllSymbols with [${symbols.size()}] count executed in [${System.currentTimeMillis() - startStopwatch}] ms]"

        symbols
    }

    List<ViewSymbolExtended> findAllViewSymbolExtendedWithParameters(Double maxQuotePrice = null, Double minQuotePrice = null) {
        viewSymbolExtendedPersistenceService.findAllWithParameters(maxQuotePrice, minQuotePrice)
    }

    /**
     * Queues backfill of all symbol_extended data (52-week statistics for each symbol and quote date, etc.
     * @return BasicResponse
     */
    BasicResponse backfillAllSymbols() {

        List<PersistableSymbol> allSymbols = symbolPersistenceService.findAllSymbols()

        allSymbols.each { symbol ->
            symbolExtendedQueueSender.queueRequest(symbol.identifier)
        }

        new BasicResponse(success: true)
    }

    /**
     * Backfill specific symbol with identifier argument.
     * @param identifier
     */
    void doBackfillForSymbol(String identifier) {

        def startStopwatch = System.currentTimeMillis()

        PersistableSymbol persistableSymbol = symbolPersistenceService.findByIdentifier(identifier)

        List<PersistableQuote> symbolQuotes = quoteService.quotePersistenceService.findAllQuotesForSymbol(persistableSymbol)
        symbolQuotes.sort { q1, q2 -> q2.quoteDate <=> q1.quoteDate }

        symbolQuotes.each { quote ->

            def back52Weeks = quote.quoteDate - 364
            def quotesForDate = symbolQuotes.findAll { (it.quoteDate <= quote.quoteDate) && (it.quoteDate >= back52Weeks) }
            def pricesForDates = quotesForDate.collect { it.price }
            def max52Weeks = quotesForDate.max { it.price }
            def min52Weeks = quotesForDate.min { it.price }
            def avg52Weeks = pricesForDates ? ((double)(pricesForDates.sum { it } / pricesForDates.size())).round(2) : 0.0d
            def standardDev = statisticsUtility.getStandardDeviation(pricesForDates)

            PersistableSymbolExtended persistableSymbolExtended = symbolExtendedPersistenceService.findBySymbolAndQuoteDate(persistableSymbol, quote.quoteDate)
            if (!persistableSymbolExtended) {
                persistableSymbolExtended = new PersistableSymbolExtended()
            }

            persistableSymbolExtended.with {
                symbol = persistableSymbol
                quoteDate = quote.quoteDate
                price = quote.price
                maximum52Weeks = max52Weeks.price
                minimum52Weeks = min52Weeks.price
                average52Weeks = avg52Weeks
                priceStandardDeviation = standardDev
            }

            try {
                persistExtendedSymbol(persistableSymbolExtended)
            } catch (Exception ex) {
                // Check for "NaN" double
                log.error "Could not save persistable symbol extended [${persistableSymbolExtended}], ex: ${ex.message}"
            }

        }

        log.info("Done backfilling extended symbol data for [${persistableSymbol.identifier}] in [${System.currentTimeMillis() - startStopwatch} ms]")
    }

    @Transactional
    void persistExtendedSymbol(PersistableSymbolExtended extended) {
        symbolExtendedPersistenceService.persistSymbolExtended(extended)
    }

    /**
     * Finds symbols that are missing quotes.
     * @return
     */
    List<PersistableSymbol> findSymbolsWithMissingQuotes() {
        // TBD
    }

}
