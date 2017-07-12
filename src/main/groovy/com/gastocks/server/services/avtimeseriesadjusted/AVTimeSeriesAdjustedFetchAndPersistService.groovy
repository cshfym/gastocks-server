package com.gastocks.server.services.avtimeseriesadjusted

import com.gastocks.server.models.domain.Symbol
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedDay
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedQuote
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.repositories.QuoteRepository
import com.gastocks.server.repositories.SymbolRepository
import com.gastocks.server.services.domain.QuotePersistenceService
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
class AVTimeSeriesAdjustedFetchAndPersistService {

    @Autowired
    AVTimeSeriesAdjustedQuoteService quoteService

    @Autowired
    QuotePersistenceService quotePersistenceService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    static final int MAX_QUOTE_DAYS = 365

    /**
     * Fetch all active symbols and persist quote response, if available.
     */
    void fetchAndPersistAllQuotes() {

        List<Symbol> activeSymbols = symbolPersistenceService.findAllActiveSymbols()

        log.info("Loaded [${String.valueOf(activeSymbols.size())}] active symbols, fetching quotes.")

        activeSymbols.eachWithIndex { symbol, index ->
            // if (index > 0) { return }
            processSymbol(symbol)
        }
    }

    /**
     * Fetch all active symbols matching the partial symbol, if available.
     * @param partial
     */
    void fetchAndPersistQuotesPartial(String partial) {

        List<Symbol> activeSymbols = symbolPersistenceService.findAllByActiveAndIdentifierStartsWith(partial)

        log.info("Loaded [${String.valueOf(activeSymbols.size())}] active symbols, fetching quotes.")

        activeSymbols.eachWithIndex { symbol, index ->
            // if (index > 0) { return }
            processSymbol(symbol)
        }
    }

    /**
     * Fetch a single symbol and persist quote response, if available.
     * @param symbol
     */
    void fetchAndPersistQuote(String sym) {

        Symbol symbol = symbolPersistenceService.findByIdentifier(sym)
        if (!symbol) {
            log.error("Could not find symbol with identifier [${sym}]!")
            return
        }

        processSymbol(symbol)
    }

    void processSymbol(Symbol symbol) {

        log.info("Begin processing for symbol [${symbol.identifier}]")

        def startStopwatch = System.currentTimeMillis()

        def quote = quoteService.getQuote(symbol.identifier)
        if (!quote) {
            // Flag symbol as inactive.
            log.warn("Quote could not be located for symbol [${symbol?.identifier}], marking symbol as inactive.")
            symbolPersistenceService.inactivateSymbol(symbol)
            return
        }

        int daysPersisted = 0
        int daysBypassed = 0

        def avQuote = (AVTimeSeriesAdjustedQuote) quote
        if (quote) {
            avQuote.dailyQuoteList.eachWithIndex { dailyQuote, ix ->
                if (ix > MAX_QUOTE_DAYS) { return }
                if (quotePersistenceService.findQuote(symbol, new Date(dailyQuote.date.millis))) {
                    log.trace("Bypassing quote for symbol [${symbol.identifier}] on [${dailyQuote.date.toString()}] as it already exists.")
                    daysBypassed++
                    return
                }

                quotePersistenceService.persistNewQuote(dailyQuote, symbol)
                daysPersisted++
            }
        }

        log.info "${daysPersisted} quotes for symbol [${symbol.identifier}] stored in [${System.currentTimeMillis() - startStopwatch} ms], " +
                "${daysBypassed} existing quotes bypassed."
    }

}
