package com.gastocks.server.jms.services.avtimeseriesadjusted

import com.gastocks.server.jms.sender.SymbolExtendedQueueSender
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedQuote
import com.gastocks.server.models.domain.jms.QueueableSymbol
import com.gastocks.server.services.IExternalQuoteService
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
class AVTimeSeriesAdjustedProcessingService {

    @Autowired
    QuotePersistenceService quotePersistenceService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    SymbolExtendedQueueSender symbolExtendedQueueSender

    @Value('${avtsa.max.quote.days}')
    Integer AVTSA_MAX_QUOTE_DAYS

    /**
     * Primary method for processing a symbol into a quote and persisting it. (Move from JMS package later?)
     * @param symbol
    */
    @Transactional
    void processSymbol(QueueableSymbol symbol, IExternalQuoteService quoteService) {

        log.info("Begin processing for symbol [${symbol.identifier}]")

        def startStopwatch = System.currentTimeMillis()

        def persistableSymbol = symbolPersistenceService.findById(symbol.symbolId)

        def quote = quoteService.getQuote(persistableSymbol.identifier)
        if (!quote) {
            log.warn("Quote could not be located for symbol [${persistableSymbol?.identifier}].")
            return
        }

        int daysPersisted = 0
        int daysBypassed = 0

        def avQuote = (AVTimeSeriesAdjustedQuote) quote
        if (quote) {
            avQuote.dailyQuoteList.eachWithIndex { dailyQuote, ix ->
                if (ix > AVTSA_MAX_QUOTE_DAYS) { return }
                if (quotePersistenceService.findQuote(persistableSymbol, new Date(dailyQuote.date.millis))) {
                   log.trace("Bypassing quote for symbol [${symbol.identifier}] on [${dailyQuote.date.toString()}] as it already exists.")
                   daysBypassed++
                   return
               }

               quotePersistenceService.persistNewQuote(dailyQuote, persistableSymbol)
               daysPersisted++
           }

            symbolExtendedQueueSender.queueRequest(persistableSymbol.identifier)
        }

        log.info "${daysPersisted} quotes for symbol [${symbol.identifier}] stored in [${System.currentTimeMillis() - startStopwatch} ms], " +
           "${daysBypassed} existing quotes bypassed."
    }

}
