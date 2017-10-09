package com.gastocks.server.models.domain

import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

@Entity(name="symbol_metadata")
class PersistableSymbolMetadata {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    PersistableSymbol symbol

    PersistableSector sector

    PersistableIndustry industry

    Long ipoYear

    BigDecimal marketCap

    @Override
    String toString() {
        "PersistableSymbolMetadata [${symbol.identifier}] - [${sector.description}] - [${industry.description}] - [${ipoYear}] - [${marketCap}]"
    }
}
