package com.gastocks.server.models.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name="v_symbol_extended")
class ViewSymbolExtended {

    @Id
    String symbolId

    String identifier

    String companyName

    String companyDescription

    String companyUrl

    String sector

    String industryCategory

    String industrySubCategory

    int employees

    String ceo

    String headquartersState

    @Column(name="max_price")
    double maxPrice

    @Column(name="min_price")
    double minPrice

    @Column(name="avg_price")
    double avgPrice

    @Column(name="max_price_stdev")
    double maxPriceStdev

    @Column(name="avg_price_stdev")
    double avgPriceStdev

}

