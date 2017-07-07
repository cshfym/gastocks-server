package com.gastocks.server.models

import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.joda.time.DateTime

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@ToString
@Entity (name="quote")
class PersistableQuote {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id


    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    DateTime createDateTime

    @ManyToOne
    @JoinColumn(name = "symbol_id")
    Symbol symbol

    Double latestPrice
    Double currentTradingDayOpen
    Double currentTradingDayHigh
    Double currentTradingDayLow
    Double previousTradingDayClose
    Double priceChange
    Float priceChangePercentage
    Integer volume
    DateTime lastMarketDateTime

}
