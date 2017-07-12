package com.gastocks.server.repositories

import com.gastocks.server.models.domain.Symbol
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface SymbolRepository extends CrudRepository<Symbol, String> {

    Symbol findByIdentifier(String identifier)

    List<Symbol> findAllByActive(Boolean active)

    @Query("SELECT s from Symbol s WHERE s.identifier LIKE CONCAT(:partial,'%') AND active = true")
    List<Symbol> findAllByActiveAndIdentifierStartsWith(@Param("partial") String partial)

}