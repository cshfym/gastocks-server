package com.gastocks.server.services.domain

import com.gastocks.server.models.avglobalquote.AVGlobalQuote
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedDay
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.repositories.QuoteRepository
import com.gastocks.server.util.DateUtility
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 *
 */
@Slf4j
@Service
class QuotePersistenceService {

    @Autowired
    QuoteRepository quoteRepository

    @Autowired
    DateUtility dateUtility

    void persistNewQuote(AVTimeSeriesAdjustedDay quote, PersistableSymbol symbol) {

        def persistableQuote = new PersistableQuote(
                symbol: symbol,
                price: quote.close,
                dayOpen: quote.dayOpen,
                dayHigh: quote.dayHigh,
                dayLow: quote.dayLow,
                volume: quote.volume,
                createTimestamp: new Date(),
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
                createTimestamp: new Date(),
                quoteDate: new Date(quote.lastUpdated.millis))

        log.debug("Saving quote: ${persistableQuote.toString()}")

        quoteRepository.save(persistableQuote)
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

        log.debug("Updating existing quote: ${existingQuote.toString()}")

        quoteRepository.save(existingQuote)
    }

    PersistableQuote findQuote(PersistableSymbol symbol, Date lastUpdated) {
        quoteRepository.findBySymbolAndQuoteDate(symbol, lastUpdated)
    }

    boolean missingQuotesForSymbol(PersistableSymbol symbol) {

        List<String> searchForDates = dateUtility.buildChronologicalDateListNoWeekends(symbol.exchangeMarket, new DateTime())

        searchForDates.each { dateString ->
            if (!quoteRepository.findBySymbolAndQuoteDate(symbol, new Date(dateString))) {
                return true
            }
        }

        false
    }
}
