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

    PersistableSymbolExtended findSymbolExtendedBySymbolAndQuoteDate(PersistableSymbol symbol, Date quoteDate) {
        symbolExtendedRepository.findBySymbolAndQuoteDate(symbol, quoteDate)
    }

}
