package com.gastocks.server.models.domain

import org.hibernate.annotations.GenericGenerator

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name="symbol")
class PersistableSymbol {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    String identifier
    String description
    Boolean active

    @ManyToOne
    @JoinColumn(name = "exchange_market_id")
    PersistableExchangeMarket exchangeMarket
}
