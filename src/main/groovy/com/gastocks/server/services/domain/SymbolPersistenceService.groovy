package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.repositories.SymbolRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class SymbolPersistenceService {

    @Autowired
    SymbolRepository symbolRepository

    PersistableSymbol findById(String id) {
        symbolRepository.findOne(id)
    }

    PersistableSymbol findByIdentifier(String identifier) {
        symbolRepository.findByIdentifier(identifier)
    }

    List<PersistableSymbol> findAllActiveSymbols() {
        symbolRepository.findAllByActive(true)
    }

    List<PersistableSymbol> findAllByActiveAndIdentifierStartsWith(String partial) {
        symbolRepository.findAllByActiveAndIdentifierStartsWith(partial)
    }

    void inactivateSymbol(PersistableSymbol symbol) {
        symbol.active = false
        symbolRepository.save(symbol)
    }
}
