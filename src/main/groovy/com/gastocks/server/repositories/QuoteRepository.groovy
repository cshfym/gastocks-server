package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import org.springframework.data.repository.CrudRepository

interface QuoteRepository extends CrudRepository<PersistableQuote, String> {

    PersistableQuote findBySymbolAndQuoteDate(PersistableSymbol symbol, Date date)

    List<PersistableQuote> findAllBySymbol(PersistableSymbol symbol)

    List<PersistableQuote> findAllBySymbolIn(List<PersistableSymbol> symbol)
}