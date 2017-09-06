package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.PersistableSymbolExtended
import com.gastocks.server.repositories.SymbolExtendedRepository
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class SymbolExtendedPersistenceService {

    @Autowired
    SymbolExtendedRepository symbolExtendedRepository

    @Transactional
    void persistSymbolExtended(PersistableSymbolExtended persistableSymbolExtended) {
        symbolExtendedRepository.save(persistableSymbolExtended)
    }

    List<PersistableSymbolExtended> findAllBySymbol(PersistableSymbol symbol) {
        def startStopwatch = System.currentTimeMillis()
        def persistableSymbolExtendedList = symbolExtendedRepository.findAllBySymbol(symbol)
        log.debug "Loaded [${persistableSymbolExtendedList.size()}] PersistableSymbolExtended for symbol [${symbol.identifier}] in [${System.currentTimeMillis() - startStopwatch}] ms"
        persistableSymbolExtendedList
    }

}
