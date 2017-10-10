package com.gastocks.server.models.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name="v_symbol_extended")
class ViewSymbolExtended {

    @Id
    String symbolId

    String identifier

    String description

    String industry

    String sector

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

