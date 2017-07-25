package com.gastocks.server.services

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.domain.QuotePersistenceService
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
}
