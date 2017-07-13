package com.gastocks.server.jms.services

import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedQuote
import com.gastocks.server.models.domain.jms.QueueableSymbol
import com.gastocks.server.services.IQuoteService
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
class AVTimeSeriesAdjustedProcessingService {

    @Autowired
    QuotePersistenceService quotePersistenceService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    static final int MAX_QUOTE_DAYS = 365

    /**
     * Primary method for processing a symbol into a quote and persisting it. (Move from JMS package later?)
     * @param symbol
    */
    @Transactional
    void processSymbol(QueueableSymbol symbol, IQuoteService quoteService) {

        log.info("Begin processing for symbol [${symbol.identifier}]")

        def startStopwatch = System.currentTimeMillis()

        def persistableSymbol = symbolPersistenceService.findById(symbol.symbolId)

        def quote = quoteService.getQuote(persistableSymbol.identifier)
        if (!quote) {
            // Flag symbol as inactive.
            log.warn("Quote could not be located for symbol [${persistableSymbol?.identifier}], marking symbol as inactive.")
            symbolPersistenceService.inactivateSymbol(persistableSymbol)
            return
        }

        int daysPersisted = 0
        int daysBypassed = 0

        def avQuote = (AVTimeSeriesAdjustedQuote) quote
        if (quote) {
            avQuote.dailyQuoteList.eachWithIndex { dailyQuote, ix ->
            if (ix > MAX_QUOTE_DAYS) { return }
            if (quotePersistenceService.findQuote(persistableSymbol, new Date(dailyQuote.date.millis))) {
               log.trace("Bypassing quote for symbol [${symbol.identifier}] on [${dailyQuote.date.toString()}] as it already exists.")
               daysBypassed++
               return
           }

           quotePersistenceService.persistNewQuote(dailyQuote, persistableSymbol)
           daysPersisted++
           }
        }

        log.info "${daysPersisted} quotes for symbol [${symbol.identifier}] stored in [${System.currentTimeMillis() - startStopwatch} ms], " +
           "${daysBypassed} existing quotes bypassed."
    }

}
