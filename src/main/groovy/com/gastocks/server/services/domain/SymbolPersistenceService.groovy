package com.gastocks.server.services.domain

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.models.domain.PersistableExchangeMarket
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.repositories.SymbolRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
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

    @Cacheable(value = CacheConfiguration.ACTIVE_SYMBOLS)
    List<PersistableSymbol> findAllActiveSymbols() {
        symbolRepository.findAllByActive(true)
    }

    @Cacheable(value = CacheConfiguration.ALL_SYMBOLS)
    List<PersistableSymbol> findAllSymbols() {
        symbolRepository.findAll()
    }

    List<PersistableSymbol> findAllByActiveAndIdentifierStartsWith(String partial, PersistableExchangeMarket exchangeMarket) {
        symbolRepository.findAllByActiveAndIdentifierStartsWith(partial, exchangeMarket.id)
    }

    void inactivateSymbol(PersistableSymbol symbol) {
        symbol.active = false
        symbolRepository.save(symbol)
    }
}
