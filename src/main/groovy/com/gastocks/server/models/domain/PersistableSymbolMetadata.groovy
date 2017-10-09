package com.gastocks.server.models.domain

import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

@Entity(name="symbol_metadata")
class PersistableSymbolMetadata {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    @OneToOne(cascade = CascadeType.ALL)
    PersistableSymbol symbol

    @OneToOne(cascade = CascadeType.ALL)
    PersistableSector sector

    @OneToOne(cascade = CascadeType.ALL)
    PersistableIndustry industry

    Long ipoYear

    BigDecimal marketCap

    @Override
    String toString() {
        "PersistableSymbolMetadata [${symbol.identifier}] - [${sector.description}] - [${industry.description}] - [${ipoYear}] - [${marketCap}]"
    }

}
