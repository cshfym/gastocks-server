package com.gastocks.server.services.domain

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.models.avglobalquote.AVGlobalQuote
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedDay
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.intrinio.IntrinioExchangePriceQuote
import com.gastocks.server.repositories.QuoteRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Service layer for quote-related persistence operations.
 */
@Slf4j
@Service
class QuotePersistenceService {

    final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")

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

        log.debug("Saving quote: ${persistableQuote.toString()} (AVGlobalQuote)")

        quoteRepository.save(persistableQuote)
    }

    void persistNewQuote(IntrinioExchangePriceQuote quote, PersistableSymbol symbol) {

        def persistableQuote = new PersistableQuote(
                symbol: symbol,
                price: quote.close,
                dayOpen: quote.open,
                dayHigh: quote.high,
                dayLow: quote.low,
                previousDayClose: 0.0d,
                priceChange: 0.0d,
                priceChangePercentage: 0.0d,
                volume: quote.volume,
                quoteDate: SHORT_DATE_FORMAT.parse(quote.date))

        log.debug("Saving quote: ${persistableQuote.toString()} (IntrinioExchangePriceQuote)")

        quoteRepository.save(persistableQuote)
    }

    /**
     * Update existing quote from AVGlobalQuote quote
     * @param existingQuote
     * @param quote
     */
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

    /**
     * Compares a {@link IntrinioExchangePriceQuote with a @link PersistableQuote}
     * @param existingQuote
     * @param quote
     * @return boolean
     */
    boolean quotesEqual(PersistableQuote existingQuote, IntrinioExchangePriceQuote quote) {

        existingQuote.price == quote.close &&
        existingQuote.dayOpen == quote.open &&
        existingQuote.dayHigh == quote.high &&
        existingQuote.dayLow == quote.low &&
        existingQuote.volume == quote.volume
    }

    /**
     * Update existing quote from AVTimeSeriesAdjustedDay quote
     * @param existingQuote
     * @param quote
     */
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

    /**
     * Update existing {@link PersistableQuote}
     * @param existingQuote
     */
    void updateQuote(PersistableQuote existingQuote) {
        quoteRepository.save(existingQuote)
    }

    PersistableQuote findQuoteBySymbolAndQuoteDate(PersistableSymbol symbol, Date lastUpdated) {
        quoteRepository.findBySymbolAndQuoteDate(symbol, lastUpdated)
    }

    @Cacheable(value = CacheConfiguration.FIND_ALL_QUOTES_FOR_SYMBOL)
    List<PersistableQuote> findAllQuotesForSymbol(PersistableSymbol symbol) {

        def startStopwatch = System.currentTimeMillis()
        def quotes = quoteRepository.findAllBySymbol(symbol)
        log.info("Loaded [${quotes?.size()}] quotes for symbol [${symbol.identifier}] in [${System.currentTimeMillis() - startStopwatch} ms]")
        quotes
    }

    void deleteQuote(PersistableQuote quote) {
        quoteRepository.delete(quote)
    }
}
