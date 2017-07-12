package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface QuoteRepository extends CrudRepository<PersistableQuote, String> {

    PersistableQuote findBySymbolAndQuoteDate(PersistableSymbol symbol, Date date)

}