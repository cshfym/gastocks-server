package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.PersistableSymbolExtended
import org.springframework.data.repository.CrudRepository

interface SymbolExtendedRepository extends CrudRepository<PersistableSymbolExtended, String> {

    PersistableSymbolExtended findBySymbolAndQuoteDate(PersistableSymbol symbol, Date quoteDate)

}