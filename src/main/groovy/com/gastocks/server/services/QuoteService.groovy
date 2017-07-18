package com.gastocks.server.services

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.util.DateUtility
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class QuoteService {

    final DateTimeFormatter SHORT_DATE_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd")

    @Autowired
    DateUtility dateUtility

    @Autowired
    QuotePersistenceService quotePersistenceService

    boolean missingQuotesForSymbol(PersistableSymbol symbol) {

        def startStopwatch = System.currentTimeMillis()

        List<String> missingDates = []

        def today = SHORT_DATE_FORMAT.parseDateTime(SHORT_DATE_FORMAT.print(new DateTime()))

        List<String> searchForDates = dateUtility.buildChronologicalDateListNoWeekends(symbol.exchangeMarket, today)

        List<PersistableQuote> allQuotesForSymbol = quotePersistenceService.findAllQuotesForSymbol(symbol)

        allQuotesForSymbol.each { quote ->
            def quoteDate = quote.quoteDate.toString()
            if (!searchForDates.contains(quoteDate)) {
                missingDates << quoteDate
            }
        }

        log.info("Found [${missingDates.size()}] missing quote dates for symbol [${symbol.identifier}] in [${System.currentTimeMillis() - startStopwatch} ms] ")

        missingDates ? true : false
    }
}
