package com.gastocks.server.services

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.converters.quote.QuoteConverter
import com.gastocks.server.jms.sender.QuoteAuditMessageSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.quote.Quote
import com.gastocks.server.services.domain.QuoteAuditPersistenceService
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import com.gastocks.server.util.DateUtility
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.text.DateFormat
import java.text.SimpleDateFormat

@Slf4j
@Service
class QuoteService {

    final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormat.forPattern("YYYY-MM-dd")
    final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd")

    @Autowired
    DateUtility dateUtility

    @Autowired
    QuotePersistenceService quotePersistenceService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    QuoteAuditPersistenceService quoteAuditPersistenceService

    @Autowired
    QuoteAuditMessageSender quoteAuditMessageSender

    @Autowired
    QuoteConverter quoteConverter

    /**
     * Retrieve all quotes for a given symbol identifier
     * Implicitly ordered by quote date descending
     * @param identifier
     * @return List<Quote>
     */
    @Cacheable(value = CacheConfiguration.GET_QUOTES_FOR_SYMBOL, key="#identifier")
    List<Quote> getQuotesForSymbol(String identifier) {

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)

        if (!symbol) { throw new QuoteNotFoundException(identifier: identifier) }

        List<PersistableQuote> persistableQuotes = quotePersistenceService.findAllQuotesForSymbol(symbol)

        // Return sorted collection of Quote objects
        def quotes = persistableQuotes.collect { persistableQuote ->
            quoteConverter.fromPersistableQuote(persistableQuote)
        }

        quotes.sort { q1, q2 -> q2.quoteDate <=> q1.quoteDate }
    }

    BasicResponse queueAllSymbolsForQuoteAudit() {

        def startStopwatch = System.currentTimeMillis()

        // Clean up all audit records prior to starting.
        try {
            quoteAuditPersistenceService.removeAllAudits()
        } catch (Exception ex) {
            log.error("Exception removing all audit records!", ex)
            return new BasicResponse(success: false, message: ex.message)
        }

        List<PersistableSymbol> allSymbols = symbolPersistenceService.findAllSymbols()
        allSymbols.each { symbol ->
            quoteAuditMessageSender.queueRequest(symbol.identifier)
        }

        log.info("Finished queueing quote audit for all symbols in [${System.currentTimeMillis() - startStopwatch} ms]")

        new BasicResponse(success: true)
    }

    @Transactional
    void runQuoteAuditForIdentifier(String identifier) {

        double allowablePriceChangePercentThreshold = 0.30  // 10% one-day jump

        def startSymbolStopwatch = System.currentTimeMillis()

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)
        List<PersistableQuote> quotesForSymbol = quotePersistenceService.findAllQuotesForSymbol(symbol)

        quotesForSymbol.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate }

        PersistableQuote lastQuote
        boolean breakSymbolSearch = false

        quotesForSymbol.eachWithIndex { quote, ix ->

            if (ix == 0 || breakSymbolSearch) {
                lastQuote = quote
                return
            }

            // Check for <= zero price
            if (quote.price <= 0.0) {
                persistQuoteAudit(symbol, quote, "Quote value <= 0.0")
                breakSymbolSearch = true
                return
            }

            // Check for irregular price jumps
            double priceChange = Math.abs(quote.price - lastQuote.price).round(3)
            double priceChangePercentage = (quote.previousDayClose > 0) ? (quote.priceChange / quote.previousDayClose).round(4) : 0.0000
            if (priceChangePercentage > allowablePriceChangePercentThreshold) {
                persistQuoteAudit(symbol, quote, "Quote price change of [${priceChange}] from [${lastQuote.price}] to [${quote.price}] " +
                        "greater than allowable threshold of [${allowablePriceChangePercentThreshold}%] (changed [${priceChangePercentage}%])")
                breakSymbolSearch = true
                return
            }

            lastQuote = quote
        }

        quotesForSymbol.clear()

        log.info("Finished quote audit for symbol [${symbol.identifier}] in [${System.currentTimeMillis() - startSymbolStopwatch} ms]")
    }

    void persistQuoteAudit(PersistableSymbol symbol, PersistableQuote quote, String auditText) {
        quoteAuditPersistenceService.persistNewQuoteAudit(symbol, quote, auditText)
    }

    boolean missingQuotesForSymbol(PersistableSymbol symbol) {

        def startStopwatch = System.currentTimeMillis()

        List<String> missingDates = []

        def today = SHORT_DATE_FORMATTER.parseDateTime(SHORT_DATE_FORMATTER.print(new DateTime()))

        List<String> searchForDates = dateUtility.buildChronologicalDateListNoWeekends(symbol.exchangeMarket, today)

        // VV Dates are coming out adjusted for time zone or some shit. :angryshower:
        List<PersistableQuote> allQuotesForSymbol = quotePersistenceService.findAllQuotesForSymbol(symbol)
        allQuotesForSymbol.sort { q1, q2 -> q2.quoteDate <=> q1.quoteDate}
        List<String> quoteDates = allQuotesForSymbol.collect { SHORT_DATE_FORMAT.format(it.quoteDate) }

        searchForDates.each { searchDate ->
            if (!quoteDates.contains(searchDate)) {
                missingDates << searchDate
            }
/*          def matchingQuote = allQuotesForSymbol.find { quote ->
              def ld1 = new LocalDate(quote.quoteDate)
              def ld2 = new LocalDate(searchDate)
              boolean result = ld1 == ld2
              log.info("Comparing [${ld1.toString()}] and [${ld2.toString()}] for equality, result: [${result}]")
             result
          }
          if (!matchingQuote) {
              missingDates << searchDate
          }
*/
        }


        // TODO How to expect quotes that don't have the same dates as the chrono date list?
        log.info("Found [${missingDates.size()}] missing quote dates for symbol [${symbol.identifier}] in [${System.currentTimeMillis() - startStopwatch} ms] ")

        missingDates ? true : false
    }
}
