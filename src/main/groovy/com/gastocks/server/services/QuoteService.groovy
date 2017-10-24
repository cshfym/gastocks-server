package com.gastocks.server.services

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.converters.quote.QuoteConverter
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.quote.Quote
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Slf4j
@Service
class QuoteService {

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
