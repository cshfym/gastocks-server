package com.gastocks.server.services

import com.gastocks.server.models.PersistableQuote
import com.gastocks.server.models.Quote
import com.gastocks.server.models.Symbol
import com.gastocks.server.repositories.QuoteRepository
import com.gastocks.server.repositories.SymbolRepository
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
@CompileStatic
@Transactional
class QuoteFetchService {

    @Autowired
    QuoteService quoteService

    @Autowired
    QuoteRepository quoteRepository

    @Autowired
    SymbolRepository symbolRepository

    /**
     * Fetch all active symbols and persist quote response, if available.
     */
    void fetchAndPersistAllQuotes() {

        List<Symbol> activeSymbols = symbolRepository.findAllByActive(true)

        log.info("Loaded [${String.valueOf(activeSymbols.size())}] active symbols, fetching quotes.")

        activeSymbols.eachWithIndex { symbol, index ->
            if (index > 2) { return }
            Quote quote = quoteService.getQuote(symbol.identifier)
            if (quote) {
                persistQuote(quote, symbol)
            }
        }

    }

    void persistQuote(Quote quote, Symbol symbol) {

        //def dtFormat = DateTimeFormat.forPattern("MM-dd-YYYY HH:mm:ss:SSS zzz")
        //def nowFormatted = new DateTime().toString(dtFormat)

        def persistableQuote = new PersistableQuote(
            symbol: symbol,
            latestPrice: quote.latestPrice,
            currentTradingDayOpen: quote.currentTradingDayOpen,
            currentTradingDayHigh: quote.currentTradingDayHigh,
            currentTradingDayLow: quote.currentTradingDayLow,
            previousTradingDayClose: quote.previousTradingDayClose,
            priceChange: quote.priceChange,
            priceChangePercentage: quote.priceChangePercentage,
            volume: quote.volume,
            createDateTime: new DateTime(),
            lastMarketDateTime: quote.lastUpdated)

        log.info("Saving quote: ${persistableQuote.toString()}")

        quoteRepository.save(persistableQuote)

    }

}
