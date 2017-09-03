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

    String exchangeMarketShortName

    @Column(name="max_price")
    double maxPrice

    @Column(name="min_price")
    double minPrice

    @Column(name="avg_price")
    double avgPrice

}

