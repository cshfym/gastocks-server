package com.gastocks.server.services

import com.gastocks.server.converters.symbol.SymbolConverter
import com.gastocks.server.jms.sender.QuotePriceChangeQueueSender
import com.gastocks.server.jms.sender.SymbolExtendedQueueSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.PersistableSymbolExtended
import com.gastocks.server.models.domain.ViewSymbolExtended
import com.gastocks.server.models.symbol.Symbol
import com.gastocks.server.services.domain.SymbolExtendedPersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import com.gastocks.server.services.domain.ViewSymbolExtendedPersistenceService
import com.gastocks.server.util.StatisticsUtility
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
class SymbolService {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    QuoteService quoteService

    @Autowired
    SymbolConverter symbolConverter

    @Autowired
    SymbolExtendedQueueSender symbolExtendedQueueSender

    @Autowired
    SymbolExtendedPersistenceService symbolExtendedPersistenceService

    @Autowired
    ViewSymbolExtendedPersistenceService viewSymbolExtendedPersistenceService

    @Autowired
    QuotePriceChangeQueueSender quotePriceChangeQueueSender

    @Autowired
    StatisticsUtility statisticsUtility

    /**
     * Finds all symbols available
     * @return List<Symbol>
     */
    List<Symbol> findAllSymbols() {

        def startStopwatch = System.currentTimeMillis()

        List<PersistableSymbol> allSymbols = symbolPersistenceService.findAllSymbols()

        def symbols = allSymbols.collect { persistableSymbol ->
            symbolConverter.fromPersistableSymbol(persistableSymbol)
        }.sort { q1, q2 -> q1.identifier <=> q2.identifier } // Ascending by identifier, i.e. MYGN

        log.info "Method findAllSymbols with [${symbols.size()}] count executed in [${System.currentTimeMillis() - startStopwatch}] ms]"

        symbols
    }

    List<ViewSymbolExtended> findAllViewSymbolExtendedWithParameters(Double maxQuotePrice = null, Double minQuotePrice = null) {
        viewSymbolExtendedPersistenceService.findAllWithParameters(maxQuotePrice, minQuotePrice)
    }

    /**
     * Queues backfill of all symbol_extended data (52-week statistics for each symbol and quote date, etc.
     * @return BasicResponse
     */
    BasicResponse backfillAllSymbols() {

        List<PersistableSymbol> allSymbols = symbolPersistenceService.findAllSymbols()

        allSymbols.each { symbol ->
            symbolExtendedQueueSender.queueRequest(symbol.identifier)
        }

        new BasicResponse(success: true)
    }

    /**
     * Backfill specific symbol with identifier argument.
     * @param identifier
     */
    @Transactional
    void doBackfillForSymbol(String identifier) {

        def startStopwatch = System.currentTimeMillis()

        PersistableSymbol persistableSymbol = symbolPersistenceService.findByIdentifier(identifier)

        List<PersistableQuote> symbolQuotes = quoteService.quotePersistenceService.findAllQuotesForSymbol(persistableSymbol)
        symbolQuotes.sort { q1, q2 -> q2.quoteDate <=> q1.quoteDate }

        List<PersistableSymbolExtended> existingPersistableSymbolExtendedList = symbolExtendedPersistenceService.findAllBySymbol(persistableSymbol)

        symbolQuotes.each { quote ->

            def back52Weeks = quote.quoteDate - 364
            def quotesForDate = symbolQuotes.findAll { (it.quoteDate <= quote.quoteDate) && (it.quoteDate >= back52Weeks) }
            def pricesForDates = quotesForDate.collect { it.price }
            def max52Weeks = quotesForDate.max { it.price }
            def min52Weeks = quotesForDate.min { it.price }
            def avg52Weeks = pricesForDates ? ((double)(pricesForDates.sum { it } / pricesForDates.size())).round(2) : 0.0d
            def standardDev = statisticsUtility.getStandardDeviation(pricesForDates)

            def persistableSymbolExtended = existingPersistableSymbolExtendedList.find {
                (it.symbol.identifier == persistableSymbol.identifier) && (it.quoteDate == quote.quoteDate)
            }

            if (!persistableSymbolExtended) {
                persistableSymbolExtended = new PersistableSymbolExtended()
            } else {
                if (symbolExtendedValuesAreIdentical(persistableSymbolExtended, max52Weeks.price, min52Weeks.price, avg52Weeks, standardDev)) {
                    return // No update needed.
                }
            }

            persistableSymbolExtended.with {
                symbol = persistableSymbol
                quoteDate = quote.quoteDate
                price = quote.price
                maximum52Weeks = max52Weeks.price
                minimum52Weeks = min52Weeks.price
                average52Weeks = avg52Weeks
                priceStandardDeviation = standardDev
            }

            symbolExtendedPersistenceService.persistSymbolExtended(persistableSymbolExtended)
        }

        log.info("Done backfilling extended symbol data for [${persistableSymbol.identifier}] in [${System.currentTimeMillis() - startStopwatch} ms]")
    }

    boolean symbolExtendedValuesAreIdentical(PersistableSymbolExtended symbolExtended, Double max52Weeks, Double min52Weeks, Double avg52Weeks, Double standardDev) {
        (symbolExtended.maximum52Weeks == max52Weeks) &&
        (symbolExtended.minimum52Weeks == min52Weeks) &&
        (symbolExtended.average52Weeks == avg52Weeks) &&
        (symbolExtended.priceStandardDeviation == standardDev)
    }

    /**
     * Queues backfill of all price change data
     * @return BasicResponse
     */
    BasicResponse backfillAllSymbolsPriceChangeData() {

        List<PersistableSymbol> allSymbols = symbolPersistenceService.findAllSymbols()

        allSymbols.each { symbol ->
            quotePriceChangeQueueSender.queueRequest(symbol.identifier)
        }

        new BasicResponse(success: true)
    }

    /**
     * Backfill price change data (previousDayClose, priceChange, priceChangePercentage) for a given symbol.
     * @param identifier
     */
    @Transactional
    void doBackfillPriceChangeData(String identifier) {

        def startStopwatch = System.currentTimeMillis()

        PersistableSymbol persistableSymbol = symbolPersistenceService.findByIdentifier(identifier)

        List<PersistableQuote> symbolQuotes = quoteService.quotePersistenceService.findAllQuotesForSymbol(persistableSymbol)
        symbolQuotes.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate }

        PersistableQuote previousQuote

        symbolQuotes.eachWithIndex { quote, index ->

            if (allPriceDataAvailable(quote)) {
                previousQuote = quote
                return
            }

            if (index == 0) {
                quote.previousDayClose = quote.price
                quote.priceChange = 0
                quote.priceChangePercentage = 0
            } else {
                quote.previousDayClose = previousQuote.price
                quote.priceChange = (quote.price - previousQuote.price).round(3)
                quote.priceChangePercentage = (quote.previousDayClose > 0) ? (quote.priceChange / quote.previousDayClose).round(4) : 0.0000
            }

            quoteService.quotePersistenceService.updateQuote(quote)

            previousQuote = quote
        }

        log.info("Done backfilling price change data for [${persistableSymbol.identifier}] in [${System.currentTimeMillis() - startStopwatch} ms]")

    }

    boolean allPriceDataAvailable(PersistableQuote quote) {
        quote.previousDayClose && quote.priceChange && quote.priceChangePercentage
    }

}
