package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.Symbol
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface QuoteRepository extends CrudRepository<PersistableQuote, String> {

    PersistableQuote findBySymbolAndQuoteDate(Symbol symbol, Date date)

}