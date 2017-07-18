package com.gastocks.server.services

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Component

@Component
class PreFetchService {

    @Autowired
    private TaskExecutor taskExecutor

    @Autowired
    QuotePersistenceService quotePersistenceService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    void preFetch() {

        // Do some work here.
    }

    /* Unused
    private class QuoteFetchClass implements Runnable {

        PersistableSymbol symbol

        QuoteFetchClass(PersistableSymbol symbol) { this.symbol = symbol }

        void run() {
            quotePersistenceService.findAllQuotesForSymbol(this.symbol) // Method is cached
        }

    }
    */

}
