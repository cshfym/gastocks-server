package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.PersistableSymbolExtended
import com.gastocks.server.repositories.SymbolExtendedRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class SymbolExtendedPersistenceService {

    @Autowired
    SymbolExtendedRepository symbolExtendedRepository

    void persistSymbolExtended(PersistableSymbolExtended persistableSymbolExtended) {
        symbolExtendedRepository.save(persistableSymbolExtended)
    }

    PersistableSymbolExtended findBySymbolAndQuoteDate(PersistableSymbol symbol, Date quoteDate) {
        symbolExtendedRepository.findBySymbolAndQuoteDate(symbol, quoteDate)
    }

    List<PersistableSymbolExtended> findAllBySymbolWithParameters(PersistableSymbol symbol, Double maxQuotePrice = null, Double minQuotePrice = null) {

        if (!maxQuotePrice && !minQuotePrice) {
            symbolExtendedRepository.findAllBySymbolOrderByQuoteDateDesc(symbol)
        } else {
            def symbolExtendedList = symbolExtendedRepository.findAllBySymbolOrderByQuoteDateDesc(symbol)
            if (symbolExtendedList.any { it.price > maxQuotePrice || it.price < minQuotePrice }) {
                return []
            }
        }

    }

}
