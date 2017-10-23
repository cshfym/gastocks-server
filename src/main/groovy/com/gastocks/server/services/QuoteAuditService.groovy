package com.gastocks.server.services

import com.gastocks.server.jms.sender.QuoteAuditMessageSender
import com.gastocks.server.jms.sender.SymbolQueueSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableQuoteAudit
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.jms.QueueableSymbol
import com.gastocks.server.services.domain.QuoteAuditPersistenceService
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import com.gastocks.server.util.DateUtility
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.text.DateFormat
import java.text.SimpleDateFormat

@Slf4j
@Service
class QuoteAuditService {

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
    SymbolQueueSender symbolQueueSender


    BasicResponse queueAllSymbolsForQuoteAudit() {

        def startStopwatch = System.currentTimeMillis()

        // Clean up all audit records prior to starting.
        try {
            quoteAuditPersistenceService.deleteAllAudits()
        } catch (Exception ex) {
            log.error("Exception removing all audit records!", ex)
            return new BasicResponse(success: false, message: ex.message)
        }

        List<PersistableSymbol> allSymbols = symbolPersistenceService.findAllActiveSymbols()
        allSymbols.each { symbol ->
            quoteAuditMessageSender.queueAuditRequest(symbol.identifier)
        }

        log.info("Finished queueing quote audit for all symbols in [${System.currentTimeMillis() - startStopwatch} ms]")

        new BasicResponse(success: true)
    }

    /**
     * Iterates all previously loaded quote_audit records, enqueue for processing.
     * @return {@link BasicResponse}
     */
    BasicResponse doQueueSymbolsForAuditReload() {

        List<PersistableQuoteAudit> quoteAuditList = quoteAuditPersistenceService.findAll()

        quoteAuditList.each { quoteAudit ->
            quoteAuditMessageSender.queueAuditReloadRequest(quoteAudit.id)
        }

        quoteAuditList.clear()

        new BasicResponse(success: true, message: "[${quoteAuditList?.size()}] quote_audit records queued.")
    }

    /**
     * Iterates all previously loaded quote_audit records, removes existing quote data, and fetches new quotes.
     * @return {@link BasicResponse}
     */
    @Transactional
    void doQuoteAuditReload(String auditId) {

        PersistableQuoteAudit quoteAudit = quoteAuditPersistenceService.findById(auditId)

        if (!quoteAudit) {
            log.warn("Could not find PersistableQuoteAudit with id [${auditId}]")
            return
        }

        PersistableSymbol symbol = symbolPersistenceService.findById(quoteAudit.symbol.id)

        quoteAuditPersistenceService.deleteAudit(quoteAudit) // Delete audit record linked to quote

        List<PersistableQuote> quotes = quotePersistenceService.findAllQuotesForSymbol(symbol)
        log.info("Found [${quotes?.size()}] quotes for symbol [${symbol.identifier}], proceeding to delete quotes.")
        def startStopwatch = System.currentTimeMillis()
        quotes.each { quote ->
            quotePersistenceService.deleteQuote(quote)
        }
        log.info("Deleted [${quotes?.size()}] quotes for symbol [${symbol.identifier}] in [${System.currentTimeMillis() - startStopwatch} ms]")

        symbolQueueSender.queueSymbol(
            new QueueableSymbol(symbolId: quoteAudit.symbol.id, identifier: quoteAudit.symbol.identifier),
            SymbolQueueSender.SYMBOL_QUEUE_DESTINATION_AVTSA
        )

        quotes.clear()

        log.info("Symbol [${symbol.identifier}] enqueued on [${SymbolQueueSender.SYMBOL_QUEUE_DESTINATION_AVTSA}] for full symbol re-load.")
    }

    @Transactional
    void runQuoteAuditForIdentifier(String identifier) {

        // Establish the set of allowable thresholds
        List<PriceChangeThreshold> thresholds = []
        thresholds << new PriceChangeThreshold(priceRangeLow: 0.000, priceRangeHigh: 9.999, thresholdPercentage: 1000.00)      //1000%
        thresholds << new PriceChangeThreshold(priceRangeLow: 10.000, priceRangeHigh: 19.999, thresholdPercentage: 2.00)       //200%
        thresholds << new PriceChangeThreshold(priceRangeLow: 20.000, priceRangeHigh: 29.999, thresholdPercentage: 1.50)       //150%
        thresholds << new PriceChangeThreshold(priceRangeLow: 30.000, priceRangeHigh: 39.999, thresholdPercentage: 1.00)       //100%
        thresholds << new PriceChangeThreshold(priceRangeLow: 40.000, priceRangeHigh: 49.999, thresholdPercentage: 1.00)       //100%
        thresholds << new PriceChangeThreshold(priceRangeLow: 50.000, priceRangeHigh: 99999999.999, thresholdPercentage: 0.50) //50%

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

            // Check for < zero price
            if (quote.price < 0.0) {
                persistQuoteAudit(symbol, quote, "Quote value < 0.0")
                breakSymbolSearch = true
                return
            }

            // Find the threshold % applicable to this price
            def thresholdRange = thresholds.find { threshold ->
                (quote.price >= threshold.priceRangeLow) && (quote.price <= threshold.priceRangeHigh)
            }

            // Check for irregular price jumps
            double priceChange = Math.abs(quote.price - lastQuote.price).round(3)
            double priceChangePercentage = (quote.price > 0) ? (priceChange / quote.price).round(4) : 0.0000
            if (priceChangePercentage > thresholdRange.thresholdPercentage) {
                persistQuoteAudit(symbol, quote, "Quote price change of [${priceChange}] from [${lastQuote.price}] to [${quote.price}] " +
                        "greater than allowable threshold of [${thresholdRange.thresholdPercentage}%] (changed [${priceChangePercentage}%])")
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

    private class PriceChangeThreshold {
        double priceRangeLow
        double priceRangeHigh
        double thresholdPercentage
    }
}
