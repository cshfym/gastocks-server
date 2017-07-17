package com.gastocks.server.schedulers

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.SymbolService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class ScheduledTasks {

    @Autowired
    SymbolService symbolService

    @Scheduled(fixedRate = 5000L)
    void process() {

        List<PersistableSymbol> symbolsWithMissingQuotes = symbolService.findSymbolsWithMissingQuotes()

        symbolsWithMissingQuotes.each { symbol ->
            log.info("Missing quote for symbol: [${symbol.identifier}]")
        }
    }
}
