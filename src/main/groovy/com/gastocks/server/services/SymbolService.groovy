package com.gastocks.server.services

import com.gastocks.server.converters.symbol.SymbolConverter
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.symbol.Symbol
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class SymbolService {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    QuoteService quoteService

    @Autowired
    SymbolConverter symbolConverter

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
}
