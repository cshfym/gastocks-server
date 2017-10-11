package com.gastocks.server.services.avtimeseriesadjusted

import com.gastocks.server.jms.sender.SymbolQueueSender
import com.gastocks.server.models.domain.PersistableExchangeMarket
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.domain.ExchangeMarketPersistenceService
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
    SymbolQueueSender queueService

    @Autowired
    ExchangeMarketPersistenceService exchangeMarketPersistenceService

    /**
     * Fetch all active symbols and persist quote response, if available.
     */
    void fetchAndPersistAllQuotes() {

        List<PersistableSymbol> symbols = symbolPersistenceService.findAllSymbols()

        log.info("Loaded [${String.valueOf(symbols.size())}] active symbols, queueing symbols for quote processing.")

        symbols.eachWithIndex { symbol, index ->
            // if (index > 0) { return }
            queueService.queueWithPersistableSymbol(symbol, SymbolQueueSender.SYMBOL_QUEUE_DESTINATION_AVTSA)
        }
    }

    /**
     * Fetch all active symbols matching the partial symbol, if available.
     * @param partial
     */
    void fetchAndPersistQuotesPartial(String partial, String exchangeName) {

        PersistableExchangeMarket market = exchangeMarketPersistenceService.findByShortName(exchangeName)
        if (!market) {
            log.error "Could not find market by exchange name [${exchangeName}]!"
            return
        }

        List<PersistableSymbol> activeSymbols = symbolPersistenceService.findAllByActiveAndIdentifierStartsWith(partial, market)

        if (activeSymbols?.size() == 0) {
          log.info("No active symbols loaded for partial match [${partial}] and exchange market [${exchangeName}], exiting.")
            return
        }

        log.info("Loaded [${String.valueOf(activeSymbols.size())}] active symbols for partial match [${partial}] and exchange market [${exchangeName}], " +
                "queueing symbols for quote processing.")

        activeSymbols.eachWithIndex { symbol, index ->
            // if (index > 0) { return }
            queueService.queueWithPersistableSymbol(symbol, SymbolQueueSender.SYMBOL_QUEUE_DESTINATION_AVTSA)
        }
    }

    /**
     * Fetch a single symbol and persist quote response, if available.
     * @param symbol
     */
    void fetchAndQueueSymbol(String identifier) {

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)
        if (!symbol) {
            log.error("Could not find symbol with identifier [${identifier}]!")
            return
        }

        queueService.queueWithPersistableSymbol(symbol, SymbolQueueSender.SYMBOL_QUEUE_DESTINATION_AVTSA)
    }

}
