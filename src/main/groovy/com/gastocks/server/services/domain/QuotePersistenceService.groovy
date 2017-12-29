package com.gastocks.server.services.domain

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.models.avglobalquote.AVGlobalQuote
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedDay
import com.gastocks.server.models.constants.GlobalConstants
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.intrinio.IntrinioExchangePriceQuote
import com.gastocks.server.repositories.QuoteRepository
import com.gastocks.server.services.redis.RedisConnectionService
import com.gastocks.server.util.NumberUtility
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut

import java.lang.reflect.Type
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Service layer for quote-related persistence operations.
 */
@Slf4j
@Service
class QuotePersistenceService {

    @Autowired
    QuoteRepository quoteRepository

    @Transactional
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

    @Transactional
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

    @Transactional
    @CachePut(value = CacheConfiguration.FIND_ALL_QUOTES_FOR_SYMBOL, key = "#symbol.id")
    PersistableQuote persistNewQuote(IntrinioExchangePriceQuote quote, PersistableSymbol symbol) {

        def persistableQuote

        try {
            persistableQuote = new PersistableQuote(
                    symbol: symbol,
                    price: NumberUtility.safeProcessDouble(quote.close),
                    dayOpen: NumberUtility.safeProcessDouble(quote.open),
                    dayHigh: NumberUtility.safeProcessDouble(quote.high),
                    dayLow: NumberUtility.safeProcessDouble(quote.low),
                    previousDayClose: 0.0d,
                    priceChange: 0.0d,
                    priceChangePercentage: 0.0d,
                    volume: NumberUtility.safeProcessInteger(quote.volume),
                    quoteDate: GlobalConstants.SHORT_DATE_FORMAT.parse(quote.date))

            log.debug("Saving quote: ${persistableQuote.toString()} (IntrinioExchangePriceQuote)")

            quoteRepository.save(persistableQuote)
        } catch (Exception ex) {
            log.error ("Exception saving quote ${persistableQuote.toString()}", ex)
        }

        persistableQuote
    }

    /**
     * Update existing quote from AVGlobalQuote quote
     * @param existingQuote
     * @param quote
     */
    @Transactional
    @CacheEvict(value = CacheConfiguration.FIND_ALL_QUOTES_FOR_SYMBOL, key = "#existingQuote.symbol.id")
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
    static boolean quotesEqual(PersistableQuote existingQuote, AVGlobalQuote quote) {

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
     * Update existing quote from AVTimeSeriesAdjustedDay quote
     * @param existingQuote
     * @param quote
     */
    @Transactional
    @CacheEvict(value = CacheConfiguration.FIND_ALL_QUOTES_FOR_SYMBOL, key = "#existingQuote.symbol.id")
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
    @Transactional
    @CacheEvict(value = CacheConfiguration.FIND_ALL_QUOTES_FOR_SYMBOL, key = "#existingQuote.symbol.id")
    void updateQuote(PersistableQuote existingQuote) {
        quoteRepository.save(existingQuote)
    }

    PersistableQuote findQuoteBySymbolAndQuoteDate(PersistableSymbol symbol, Date lastUpdated) {
        quoteRepository.findBySymbolAndQuoteDate(symbol, lastUpdated)
    }

    @Cacheable(value = CacheConfiguration.FIND_ALL_QUOTES_FOR_SYMBOL, key = "#symbol.id")
    List<PersistableQuote> findAllQuotesForSymbol(PersistableSymbol symbol) {

        def startStopwatch = System.currentTimeMillis()
        def quotes = quoteRepository.findAllBySymbol(symbol)
        log.info("Loaded [${quotes?.size()}] quotes for symbol [${symbol.identifier}] in [${System.currentTimeMillis() - startStopwatch} ms]")
        quotes
    }

    List<PersistableQuote> findAllQuotesForSymbolsByDateIn(Date quoteDate, List<PersistableSymbol> symbols) {
        def startStopwatch = System.currentTimeMillis()
        def quotes = quoteRepository.findAllByQuoteDateAndSymbolIn(quoteDate, symbols)
        log.info("Loaded [${quotes?.size()}] quotes for [${symbols.size()}] symbols on [${quoteDate.toString()}] in [${System.currentTimeMillis() - startStopwatch} ms]")
        quotes
    }

    @Transactional
    @CacheEvict(value = CacheConfiguration.FIND_ALL_QUOTES_FOR_SYMBOL, key = "#quote.symbol.id")
    void deleteQuote(PersistableQuote quote) {
        quoteRepository.delete(quote)
    }

    /**
     * Caching Stuff
     */

    @Autowired
    RedisConnectionService redisConnectionService

    Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat(GlobalConstants.DB_DATE_FORMAT_STRING)
            .create()

    List<PersistableQuote> getAllQuotesFromCache(String identifier) {

        String stringResults = redisConnectionService.getFromCache(identifier)

        if (!stringResults) { return null }

        Type collectionType = new TypeToken<List<PersistableQuote>>(){}.getType()
        (List<PersistableQuote>) gson.fromJson(stringResults, collectionType)
    }

    void putAllQuotesInCache(String identifier, List<PersistableQuote> persistableQuotes) {

        String rawCollectionString = gson.toJson(persistableQuotes)

        redisConnectionService.setCache(identifier, rawCollectionString)
    }
}
