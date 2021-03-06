package com.gastocks.server.models.domain

import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

@Entity(name="symbol_extended")
class PersistableSymbolExtended {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    @Temporal(TemporalType.DATE)
    Date quoteDate

    @ManyToOne
    @JoinColumn(name = "symbol_id")
    PersistableSymbol symbol

    @Column(name="price")
    double price

    @Column(name="average_52_weeks")
    double average52Weeks

    @Column(name="maximum_52_weeks")
    double maximum52Weeks

    @Column(name="minimum_52_weeks")
    double minimum52Weeks

    @Column(name="price_standard_deviation")
    double priceStandardDeviation

    @Override
    String toString() {
        "PersistableSymbolEtended id: [${id}], quoteDate: [${quoteDate}], symbol: [${symbol.identifier}], price: [${price}]"
    }
}
