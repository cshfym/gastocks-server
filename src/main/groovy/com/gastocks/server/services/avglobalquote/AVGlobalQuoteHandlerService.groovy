package com.gastocks.server.services.avglobalquote

import com.gastocks.server.jms.services.SymbolQueueService
import com.gastocks.server.models.domain.PersistableSymbol
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
class AVGlobalQuoteHandlerService {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    SymbolQueueService queueService

    /**
     * Fetch all active symbols and queue for handling.
     */
    void fetchAndQueueAllQuotesForAllSymbols() {

        List<PersistableSymbol> activeSymbols = symbolPersistenceService.findAllActiveSymbols()

        log.info("Loaded [${String.valueOf(activeSymbols.size())}] active symbols, queueing symbols for quote processing.")

        activeSymbols.eachWithIndex { symbol, index ->
            queueService.queueSymbol(symbol, SymbolQueueService.SYMBOL_QUEUE_DESTINATION_AVGQ)
        }
    }

}
