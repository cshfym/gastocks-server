package com.gastocks.server.services

import com.gastocks.server.converters.quote.QuoteConverter
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.quote.Quote
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
    QuoteConverter quoteConverter

    /**
     * Retrieve all quotes for a given symbol identifier
     * Implicitly ordered by quote date descending
     * @param identifier
     * @return List<Quote>
     */
    @Cacheable(value = "getQuotesForSymbol", key="#identifier")
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

    List<Double> get52WeekMinMaxForSymbolAndDate(PersistableSymbol symbol, Date date52Weeks) {
        def startStopwatch = System.currentTimeMillis()
        def minMaxForSymbol = quotePersistenceService.get52WeekMinMaxForSymbolAndDate(symbol, date52Weeks)
        log.info("Found min/max for symbol [${symbol.identifier}] in [${System.currentTimeMillis() - startStopwatch} ms] ")
        minMaxForSymbol
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
