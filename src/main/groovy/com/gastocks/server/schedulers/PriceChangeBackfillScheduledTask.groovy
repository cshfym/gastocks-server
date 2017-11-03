package com.gastocks.server.schedulers

import com.gastocks.server.jms.sender.QuotePriceChangeQueueSender
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class PriceChangeBackfillScheduledTask {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    QuotePriceChangeQueueSender quotePriceChangeQueueSender

    @Scheduled(cron = '${quote.price.change.backfill.daily.schedule}')
    void process() {

        List<PersistableSymbol> symbols = symbolPersistenceService.findAllActiveSymbols()

        log.info("Loaded [${String.valueOf(symbols.size())}] active symbols, queueing symbols for price change backfill processing.")

        symbols.each { PersistableSymbol symbol ->
            quotePriceChangeQueueSender.queueRequest(symbol.identifier)
        }
    }
}
