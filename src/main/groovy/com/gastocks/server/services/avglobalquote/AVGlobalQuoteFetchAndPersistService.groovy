package com.gastocks.server.services.avglobalquote

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.avglobalquote.AVGlobalQuote
import com.gastocks.server.models.domain.Symbol
import com.gastocks.server.repositories.QuoteRepository
import com.gastocks.server.repositories.SymbolRepository
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Service to fetch quotes from the {@AVGlobalQuoteService} and store them as {@PersistableQuote}
 */
@Slf4j
@Service
@CompileStatic
@Transactional
class AVGlobalQuoteFetchAndPersistService {

    @Autowired
    AVGlobalQuoteService avGlobalQuoteService

    @Autowired
    QuotePersistenceService quotePersistenceService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    /**
     * Fetch all active symbols and persist quote response, if available.
     */
    void fetchAndPersistAllQuotes() {

        List<Symbol> activeSymbols = symbolPersistenceService.findAllActiveSymbols()

        log.info("Loaded [${String.valueOf(activeSymbols.size())}] active symbols, fetching quotes.")

        activeSymbols.eachWithIndex { symbol, index ->
            if (index > 2) { return }
            def quote = avGlobalQuoteService.getQuote(symbol.identifier)
            def avQuote = (AVGlobalQuote) quote
            if (quote) {
                def existingQuote = quotePersistenceService.findQuote(symbol, new Date(avQuote.lastUpdated.millis))
                if (existingQuote) {
                    quotePersistenceService.updateQuote(existingQuote, avQuote)
                } else {
                    quotePersistenceService.persistNewQuote(avQuote, symbol)
                }
            }
        }
    }

}
