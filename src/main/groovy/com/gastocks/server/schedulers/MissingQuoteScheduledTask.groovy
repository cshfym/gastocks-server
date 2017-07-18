package com.gastocks.server.schedulers

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.SymbolService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Slf4j
@Component
class MissingQuoteScheduledTask {

    @Autowired
    SymbolService symbolService

    @PostConstruct
    void onPostConstruct() {
        process()
    }

    @Scheduled(cron = "0 1 1 * * ?") // Every day at 1:01 AM
    void process() {

        List<PersistableSymbol> symbolsWithMissingQuotes = symbolService.findSymbolsWithMissingQuotes()

        symbolsWithMissingQuotes.each { symbol ->
            log.info("Missing quote for symbol: [${symbol.identifier}]")
        }
    }
}
