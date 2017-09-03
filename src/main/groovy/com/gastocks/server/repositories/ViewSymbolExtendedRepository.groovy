package com.gastocks.server.repositories

import com.gastocks.server.models.domain.ViewSymbolExtended
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface ViewSymbolExtendedRepository extends CrudRepository<ViewSymbolExtended, String> {

    /*
    ViewSymbolExtended findBySymbolId(String symbolId)

    ViewSymbolExtended findByIdentifier(String identifier)

    @Query("SELECT vse FROM v_symbol_extended vse WHERE (vse.maxPrice <= :maxPrice AND vse.minPrice >= :minPrice)")
    List<ViewSymbolExtended> findAllWithParameters(@Param("maxPrice") double maxPrice, @Param("minPrice") double minPrice)
    */

}