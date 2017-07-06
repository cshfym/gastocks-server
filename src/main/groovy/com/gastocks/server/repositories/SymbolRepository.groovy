package com.gastocks.server.repositories

import com.gastocks.server.models.Symbol

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface SymbolRepository extends CrudRepository<Symbol, String> {

    Symbol findByIdentifier(String identifier)
}