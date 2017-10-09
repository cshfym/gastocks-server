package com.gastocks.server.services.domain

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.models.avglobalquote.AVGlobalQuote
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedDay
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.repositories.QuoteRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 * Service layer for quote-related persistence operations.
 */
@Slf4j
@Service
class QuotePersistenceService {

    @Autowired
    QuoteRepository quoteRepository

    void persistNewQuote(AVTimeSeriesAdjustedDay quote, PersistableSymbol symbol) {

        def persistableQuote = new PersistableQuote(
                symbol: symbol,
                price: quote.close,
                dayOpen: quote.dayOpen,
                dayHigh: quote.dayHigh,
                dayLow: quote.dayLow,
                volume: quote.volume,
                quoteDate: new Date(quote.date.millis))

        log.debug("Saving quote: ${persistableQuote.toString()}")

        quoteRepository.save(persistableQuote)
    }

    void persistNewQuote(AVGlobalQuote quote, PersistableSymbol symbol) {

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
                quoteDate: new Date(quote.lastUpdated.millis))

        log.debug("Saving quote: ${persistableQuote.toString()}")

        quoteRepository.save(persistableQuote)
    }


    void updateQuote(PersistableQuote existingQuote, AVGlobalQuote quote) {

        if (quotesEqual(existingQuote, quote)) {
            log.debug("Updating quote bypassed, quotes are identical: ${existingQuote.toString()}")
            return
        }

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

        log.debug("Updating existing quote: ${existingQuote.toString()}")

        quoteRepository.save(existingQuote)
    }

    /**
     * Compares a {@link PersistableQuote with a @link AVGlobalQuote}
     * @param existingQuote
     * @param quote
     * @return boolean
     */
    boolean quotesEqual(PersistableQuote existingQuote, AVGlobalQuote quote) {

        existingQuote.price == quote.latestPrice &&
        existingQuote.dayOpen == quote.currentTradingDayOpen &&
        existingQuote.dayHigh == quote.currentTradingDayHigh &&
        existingQuote.dayLow == quote.currentTradingDayLow &&
        existingQuote.previousDayClose == quote.previousTradingDayClose &&
        existingQuote.priceChange == quote.priceChange &&
        existingQuote.priceChangePercentage == quote.priceChangePercentage &&
        existingQuote.volume == quote.volume
    }

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

        log.debug("Updating existing quote: ${existingQuote.toString()}")

        quoteRepository.save(existingQuote)
    }

    PersistableQuote findQuote(PersistableSymbol symbol, Date lastUpdated) {
        quoteRepository.findBySymbolAndQuoteDate(symbol, lastUpdated)
    }

    @Cacheable(value = CacheConfiguration.FIND_ALL_QUOTES_FOR_SYMBOL)
    List<PersistableQuote> findAllQuotesForSymbol(PersistableSymbol symbol) {

        def startStopwatch = System.currentTimeMillis()
        def quotes = quoteRepository.findAllBySymbol(symbol)
        log.info("Loaded [${quotes?.size()}] quotes for symbol [${symbol.identifier}] in [${System.currentTimeMillis() - startStopwatch} ms]")
        quotes
    }

}
