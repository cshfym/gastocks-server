package com.gastocks.server.services

import com.gastocks.server.converters.symbol.EnhancedSymbolConverter
import com.gastocks.server.converters.symbol.SymbolConverter
import com.gastocks.server.jms.sender.SymbolExtendedQueueSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.PersistableSymbolExtended
import com.gastocks.server.models.quote.Quote
import com.gastocks.server.models.symbol.EnhancedSymbol
import com.gastocks.server.models.symbol.Symbol
import com.gastocks.server.services.domain.SymbolExtendedPersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
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
    EnhancedSymbolConverter enhancedSymbolConverter

    @Autowired
    SymbolExtendedQueueSender symbolExtendedQueueSender

    @Autowired
    SymbolExtendedPersistenceService symbolExtendedPersistenceService

    /**
     * Finds symbols that are missing quotes.
     * @return
     */
    List<PersistableSymbol> findSymbolsWithMissingQuotes() {

        List<PersistableSymbol> activeSymbols = symbolPersistenceService.findAllActiveSymbols()

        List<PersistableSymbol> symbolsWithMissingQuotes = []

        activeSymbols.each { symbol ->
            if (quoteService.missingQuotesForSymbol(symbol)) {
                symbolsWithMissingQuotes << symbol
            }
        }

        symbolsWithMissingQuotes
    }

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

    List<EnhancedSymbol> findAllEnhancedSymbols(double high52Week, double low52Week) {

        def startStopwatch = System.currentTimeMillis()

        /*
        List<PersistableSymbol> allSymbols = symbolPersistenceService.findAllSymbols()

        Date date52WeeksBack = new Date() - 364

        def enhancedSymbols = allSymbols.collect { symbol ->

            // Find min, max, avg with date constraint.

            enhancedSymbolConverter.fromPersistableSymbol(symbol, minMaxAvgForSymbol)
        }.sort { q1, q2 -> q1.identifier <=> q2.identifier } // Ascending by identifier, i.e. MYGN

        log.info "Method findAllEnhancedSymbols with [${enhancedSymbols.size()}] count executed in [${System.currentTimeMillis() - startStopwatch}] ms]"

        enhancedSymbols

        */

        null
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

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)

        List<PersistableQuote> symbolQuotes = quoteService.quotePersistenceService.findAllQuotesForSymbol(symbol)
        symbolQuotes.each { quote ->

            // Ignore duplicates.
            if (symbolExtendedPersistenceService.findSymbolExtendedBySymbolAndQuoteDate(symbol, quote.quoteDate)) { return }

            def back52Weeks = quote.quoteDate - 364
            def quotesForDate = symbolQuotes.findAll { it.quoteDate >= back52Weeks }
            def pricesForDates = quotesForDate.collect { it.price }
            def maximum52Weeks = quotesForDate.max { it.price }
            def minimum52Weeks = quotesForDate.min { it.price }
            def average52Weeks = pricesForDates ? ((double)(pricesForDates.sum { it } / pricesForDates.size())).round(2) : 0.0d

            PersistableSymbolExtended extended = new PersistableSymbolExtended(
                symbol: symbol,
                quoteDate: quote.quoteDate,
                maximum52Weeks: maximum52Weeks.price,
                minimum52Weeks: minimum52Weeks.price,
                average52Weeks: average52Weeks
            )

            persistExtendedSymbol(extended)
        }

        log.info("Done backfilling extended symbol data for [${symbol.identifier}] in [${System.currentTimeMillis() - startStopwatch} ms]")
    }

    @Transactional
    void persistExtendedSymbol(PersistableSymbolExtended extended) {
        symbolExtendedPersistenceService.persistSymbolExtended(extended)
    }
}
