package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.Symbol
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

    Symbol findByIdentifier(String identifier) {
        symbolRepository.findByIdentifier(identifier)
    }

    List<Symbol> findAllActiveSymbols() {
        symbolRepository.findAllByActive(true)
    }

    List<Symbol> findAllByActiveAndIdentifierStartsWith(String partial) {
        symbolRepository.findAllByActiveAndIdentifierStartsWith(partial)
    }

    void inactivateSymbol(Symbol symbol) {
        symbol.active = false
        symbolRepository.save(symbol)
    }
}
