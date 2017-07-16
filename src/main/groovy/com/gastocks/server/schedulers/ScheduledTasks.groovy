package com.gastocks.server.schedulers

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.avglobalquote.AVGlobalQuoteService
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class ScheduledTasks {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    AVGlobalQuoteService quoteService

    @Scheduled(fixedRate = 5000L)
    void process() {

        List<PersistableSymbol> symbolsWithMissingQuotes = symbolPersistenceService.findSymbolsWithMissingQuotes()

        symbolsWithMissingQuotes.each { symbol ->
            log.info("Missing quote for symbol: [${symbol.identifier}]")
        }
    }
}
