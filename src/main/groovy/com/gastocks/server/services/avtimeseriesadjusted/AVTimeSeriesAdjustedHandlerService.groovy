package com.gastocks.server.services.avtimeseriesadjusted

import com.gastocks.server.jms.services.SymbolQueueService
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Service to fetch quotes from the {@AVTimeSeriesAdjustedQuoteService} and store them as {@PersistableQuote}
 */
@Slf4j
@Service
@CompileStatic
@Transactional
class AVTimeSeriesAdjustedHandlerService {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    SymbolQueueService queueService

    /**
     * Fetch all active symbols and persist quote response, if available.
     */
    void fetchAndPersistAllQuotes() {

        List<PersistableSymbol> activeSymbols = symbolPersistenceService.findAllActiveSymbols()

        log.info("Loaded [${String.valueOf(activeSymbols.size())}] active symbols, queueing symbols for quote processing.")

        activeSymbols.eachWithIndex { symbol, index ->
            // if (index > 0) { return }
            queueService.queueSymbol(symbol, SymbolQueueService.SYMBOL_QUEUE_DESTINATION_AVTSA)
        }
    }

    /**
     * Fetch all active symbols matching the partial symbol, if available.
     * @param partial
     */
    void fetchAndPersistQuotesPartial(String partial) {

        List<PersistableSymbol> activeSymbols = symbolPersistenceService.findAllByActiveAndIdentifierStartsWith(partial)

        log.info("Loaded [${String.valueOf(activeSymbols.size())}] active symbols, queueing symbols for quote processing.")

        activeSymbols.eachWithIndex { symbol, index ->
            // if (index > 0) { return }
            queueService.queueSymbol(symbol, SymbolQueueService.SYMBOL_QUEUE_DESTINATION_AVTSA)
        }
    }

    /**
     * Fetch a single symbol and persist quote response, if available.
     * @param symbol
     */
    void fetchAndPersistQuote(String sym) {

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(sym)
        if (!symbol) {
            log.error("Could not find symbol with identifier [${sym}]!")
            return
        }

        queueService.queueSymbol(symbol, SymbolQueueService.SYMBOL_QUEUE_DESTINATION_AVTSA)
    }

}
