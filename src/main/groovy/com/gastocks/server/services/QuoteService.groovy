package com.gastocks.server.services

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.converters.quote.QuoteConverter
import com.gastocks.server.jms.sender.QuoteAuditMessageSender
import com.gastocks.server.jms.sender.SymbolQueueSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableQuoteAudit
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.jms.QueueableSymbol
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
    SymbolQueueSender symbolQueueSender

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

}
