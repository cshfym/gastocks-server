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
            if (index > 2) { return }

            def startStopwatch = System.currentTimeMillis()

            def quote = quoteService.getQuote(symbol.identifier)
            def avQuote = (AVTimeSeriesAdjustedQuote) quote
            if (quote) {
                avQuote.dailyQuoteList.eachWithIndex { dailyQuote, ix ->
                    if (ix > MAX_QUOTE_DAYS) { return }
                    persistNewQuote(dailyQuote, symbol)
                }
            }
            log.info "Quotes for symbol [${symbol}] stored in [${System.currentTimeMillis() - startStopwatch} ms]"
        }
    }

    /*
    PersistableQuote findQuote(Symbol symbol, Date quoteDate) {
        quoteRepository.findBySymbolAndQuoteDate(symbol, quoteDate)
    }
    */

    /*
    void updateQuote(PersistableQuote existingQuote, AVTimeSeriesAdjustedDay quote) {

        existingQuote.with {
            price = quote.close
            dayOpen = quote.dayOpen
            dayHigh = quote.dayHigh
            dayLow = quote.dayLow
            volume = quote.volume
            dividend = quote.dividend
            splitCoefficient = quote.splitCoefficient
        }

        // log.info("Updating existing quote: ${existingQuote.toString()}")

        quoteRepository.save(existingQuote)
    }
    */

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
