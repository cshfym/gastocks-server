package com.gastocks.server.jms.services.avglobalquote

import com.gastocks.server.models.avglobalquote.AVGlobalQuote
import com.gastocks.server.models.domain.jms.QueueableSymbol
import com.gastocks.server.services.IExternalQuoteService
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
class AVGlobalQuoteProcessingService {

    @Autowired
    QuotePersistenceService quotePersistenceService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    /**
     * Primary method for processing a symbol into a quote and persisting it. (Move from JMS package later?)
     * @param symbol
    */
    @Transactional
    void processSymbol(QueueableSymbol symbol, IExternalQuoteService quoteService) {

        log.info("Begin processing for symbol [${symbol.identifier}]")

        def persistableSymbol = symbolPersistenceService.findById(symbol.symbolId)

        def quote = quoteService.getQuote(persistableSymbol.identifier)
        if (!quote) {
            log.warn("Quote could not be located for symbol [${persistableSymbol?.identifier}].")
            return
        }

        def avQuote = (AVGlobalQuote) quote
        if (avQuote) {
             def existingQuote = quotePersistenceService.findQuote(persistableSymbol, new Date(avQuote.lastUpdated.millis))
             if (existingQuote) {
                 quotePersistenceService.updateQuote(existingQuote, avQuote)
                 log.info ("Updating existing quote for [${persistableSymbol.identifier} - ${persistableSymbol.exchangeMarket.shortName}] on [${avQuote.lastUpdated.toString()}]")
             } else {
                 quotePersistenceService.persistNewQuote(avQuote, persistableSymbol)
                 log.info("Persisting new quote for [${persistableSymbol.identifier} - ${persistableSymbol.exchangeMarket.shortName}] on [${avQuote.lastUpdated.toString()}]")
            }
        }

    }

}
