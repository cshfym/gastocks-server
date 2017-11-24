package com.gastocks.server.services.domain

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.models.avglobalquote.AVGlobalQuote
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedDay
import com.gastocks.server.models.domain.PersistableCompany
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.intrinio.IntrinioExchangePriceQuote
import com.gastocks.server.models.sector.TechnicalSectorQuote
import com.gastocks.server.repositories.CompanyRepository
import com.gastocks.server.repositories.QuoteRepository
import com.gastocks.server.repositories.SectorRepository
import com.gastocks.server.services.intrinio.company.CompanyService
import com.gastocks.server.util.NumberUtility
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

import javax.transaction.Transactional
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

    @Autowired
    SectorPersistenceService sectorPersistenceService

    @Autowired
    CompanyService companyService

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
    void persistNewQuote(IntrinioExchangePriceQuote quote, PersistableSymbol symbol) {

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
                    quoteDate: SHORT_DATE_FORMAT.parse(quote.date))

            log.debug("Saving quote: ${persistableQuote.toString()} (IntrinioExchangePriceQuote)")

            quoteRepository.save(persistableQuote)
        } catch (Exception ex) {
            log.error ("Exception saving quote ${persistableQuote.toString()}", ex)
        }

    }

    /**
     * Update existing quote from AVGlobalQuote quote
     * @param existingQuote
     * @param quote
     */
    @Transactional
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
     * Update existing quote from AVTimeSeriesAdjustedDay quote
     * @param existingQuote
     * @param quote
     */
    @Transactional
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

    List<PersistableQuote> findAllQuotesForSymbolsIn(List<PersistableSymbol> symbols) {
        def startStopwatch = System.currentTimeMillis()
        def quotes = quoteRepository.findAllBySymbolIn(symbols)
        log.info("Loaded [${quotes?.size()}] quotes for [${symbols.size()}] symbols in [${System.currentTimeMillis() - startStopwatch} ms]")
        quotes
    }

    @Transactional
    void deleteQuote(PersistableQuote quote) {
        quoteRepository.delete(quote)
    }
}
