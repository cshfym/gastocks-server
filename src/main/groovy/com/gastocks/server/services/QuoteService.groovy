package com.gastocks.server.services

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.util.DateUtility
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.beans.factory.annotation.Autowired
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
