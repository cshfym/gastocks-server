package com.gastocks.server.services

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.util.DateUtility
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class QuoteService {

    @Autowired
    DateUtility dateUtility

    @Autowired
    QuotePersistenceService quotePersistenceService

    boolean missingQuotesForSymbol(PersistableSymbol symbol) {

        List<String> searchForDates = dateUtility.buildChronologicalDateListNoWeekends(symbol.exchangeMarket, new DateTime())

        // TODO Load all symbols for a quote, and iterate

        searchForDates.each { dateString ->
            if (!quotePersistenceService.findQuote(symbol, new Date(DateTime.parse(dateString).millis))) {
                return true
            }
        }

        false
    }
}
