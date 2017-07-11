package com.gastocks.server.services.avtimeseriesadjusted

import com.gastocks.server.models.Symbol
import com.gastocks.server.models.avglobalquote.AVGlobalQuote
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedDay
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedQuote
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.repositories.QuoteRepository
import com.gastocks.server.repositories.SymbolRepository
import com.gastocks.server.services.avglobalquote.AVGlobalQuoteService
import com.gastocks.server.util.DateUtility
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
    QuoteRepository quoteRepository

    @Autowired
    SymbolRepository symbolRepository

    static final int MAX_QUOTE_DAYS = 365

    /**
     * Fetch all active symbols and persist quote response, if available.
     */
    void fetchAndPersistAllQuotes() {

        List<Symbol> activeSymbols = symbolRepository.findAllByActive(true)

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

        List<Symbol> activeSymbols = symbolRepository.findAllByActiveAndIdentifierStartsWith(partial)

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

        Symbol symbol = symbolRepository.findByIdentifier(sym)
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
            inactivateSymbol(symbol)
            return
        }

        int daysPersisted = 0
        int daysBypassed = 0

        def avQuote = (AVTimeSeriesAdjustedQuote) quote
        if (quote) {
            avQuote.dailyQuoteList.eachWithIndex { dailyQuote, ix ->
                if (ix > MAX_QUOTE_DAYS) { return }
                if (findQuote(symbol, new Date(dailyQuote.date.millis))) {
                    log.trace("Bypassing quote for symbol [${symbol.identifier}] on [${dailyQuote.date.toString()}] as it already exists.")
                    daysBypassed++
                    return
                }

                persistNewQuote(dailyQuote, symbol)
                daysPersisted++
            }
        }

        log.info "${daysPersisted} quotes for symbol [${symbol.identifier}] stored in [${System.currentTimeMillis() - startStopwatch} ms], " +
                "${daysBypassed} existing quotes bypassed."
    }

    PersistableQuote findQuote(Symbol symbol, Date quoteDate) {
        quoteRepository.findBySymbolAndQuoteDate(symbol, quoteDate)
    }

    void inactivateSymbol(Symbol symbol) {
        symbol.active = false
        symbolRepository.save(symbol)
    }

    void persistNewQuote(AVTimeSeriesAdjustedDay quote, Symbol symbol) {

        def persistableQuote = new PersistableQuote(
            symbol: symbol,
            price: quote.close,
            dayOpen: quote.dayOpen,
            dayHigh: quote.dayHigh,
            dayLow: quote.dayLow,
            volume: quote.volume,
            createTimestamp: new Date(),
            quoteDate: new Date(quote.date.millis))

        quoteRepository.save(persistableQuote)
    }

}
