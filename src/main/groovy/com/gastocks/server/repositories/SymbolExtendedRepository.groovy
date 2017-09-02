package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.PersistableSymbolExtended
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface SymbolExtendedRepository extends CrudRepository<PersistableSymbolExtended, String> {

    PersistableSymbolExtended findBySymbolAndQuoteDate(PersistableSymbol symbol, Date quoteDate)

    @Query("SELECT se from symbol_extended se WHERE se.symbol = :symbol AND (se.maximum_52_weeks )")
    List<PersistableSymbolExtended> findAllBySymbolAndMax52WeeksAndMin52Weeks(@Param("symbol") PersistableSymbol symbol,
                                                                              @Param("max52Weeks") double max52Weeks, @Param("min52Weeks") double min52Weeks)
}