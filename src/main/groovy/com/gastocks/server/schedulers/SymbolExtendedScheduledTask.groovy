package com.gastocks.server.schedulers

import com.gastocks.server.jms.sender.SymbolExtendedQueueSender
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class SymbolExtendedScheduledTask {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    SymbolExtendedQueueSender symbolExtendedQueueSender

    @Scheduled(cron = '${symbol.extended.backfill.daily.schedule}')
    void process() {

        List<PersistableSymbol> symbols = symbolPersistenceService.findAllActiveSymbols()

        log.info("Loaded [${String.valueOf(symbols.size())}] active symbols, queueing symbols for extended data backfill processing.")

        symbols.each { PersistableSymbol symbol ->
            symbolExtendedQueueSender.queueRequest(symbol.identifier)
        }
    }
}
