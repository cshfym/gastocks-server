package com.gastocks.server.services.avglobalquote

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.avglobalquote.AVGlobalQuote
import com.gastocks.server.models.Symbol
import com.gastocks.server.repositories.QuoteRepository
import com.gastocks.server.repositories.SymbolRepository
import com.gastocks.server.services.avglobalquote.AVGlobalQuoteService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Service to fetch quotes from the {@AVGlobalQuoteService} and store them as {@PersistableQuote}
 */
@Slf4j
@Service
@CompileStatic
@Transactional
class AVGlobalQuoteFetchAndPersistService {

    @Autowired
    AVGlobalQuoteService avGlobalQuoteService

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
            AVGlobalQuote quote = avGlobalQuoteService.getQuote(symbol.identifier)
            if (quote) {
                def existingQuote = findQuote(symbol, new Date(quote.lastUpdated.millis))
                if (existingQuote) {
                    updateQuote(existingQuote, quote)
                } else {
                    persistNewQuote(quote, symbol)
                }
            }
        }
    }

    PersistableQuote findQuote(Symbol symbol, Date lastUpdated) {
        quoteRepository.findBySymbolAndLastMarketDate(symbol, lastUpdated)
    }

    void updateQuote(PersistableQuote existingQuote, AVGlobalQuote quote) {

        existingQuote.with {
            price = quote.latestPrice
            dayOpen = quote.currentTradingDayOpen
            dayHigh = quote.currentTradingDayHigh
            dayLow = quote.currentTradingDayLow
            previousDayClose = quote.previousTradingDayClose
            priceChange = quote.priceChange
            priceChangePercentage = quote.priceChangePercentage
            volume = quote.volume
        }

        log.info("Updating existing quote: ${existingQuote.toString()}")

        quoteRepository.save(existingQuote)
    }
    
    void persistNewQuote(AVGlobalQuote quote, Symbol symbol) {

        def persistableQuote = new PersistableQuote(
            symbol: symbol,
            price: quote.latestPrice,
            dayOpen: quote.currentTradingDayOpen,
            dayHigh: quote.currentTradingDayHigh,
            dayLow: quote.currentTradingDayLow,
            previousDayClose: quote.previousTradingDayClose,
            priceChange: quote.priceChange,
            priceChangePercentage: quote.priceChangePercentage,
            volume: quote.volume,
            createTimestamp: new DateTime().millis,
            lastMarketDate: new Date(quote.lastUpdated.millis))

        log.info("Saving quote: ${persistableQuote.toString()}")

        quoteRepository.save(persistableQuote)
    }

}
