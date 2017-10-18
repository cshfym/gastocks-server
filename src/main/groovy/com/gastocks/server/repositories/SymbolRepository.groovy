package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableSymbol
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface SymbolRepository extends CrudRepository<PersistableSymbol, String> {

    PersistableSymbol findByIdentifier(String identifier)

    List<PersistableSymbol> findAllByActive(Boolean active)

    List<PersistableSymbol> findAll()

    List<PersistableSymbol> findByIdentifierIn(List<String> identifiers)

    @Query("SELECT s from symbol s WHERE s.identifier LIKE CONCAT(:partial,'%') AND active = true AND exchange_market_id = :exId")
    List<PersistableSymbol> findAllByActiveAndIdentifierStartsWith(@Param("partial") String partial, @Param("exId") String exId)

}