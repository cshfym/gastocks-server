package com.gastocks.server.models.domain

import com.gastocks.server.models.Symbol
import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Temporal
import javax.persistence.TemporalType

/**
 * Domain object compatible with any quote
 */
@ToString
@Entity (name="quote")
class PersistableQuote {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    @Temporal(TemporalType.DATE)
    Date createTimestamp

    @ManyToOne
    @JoinColumn(name = "symbol_id")
    Symbol symbol

    Double price
    Double dayOpen
    Double dayHigh
    Double dayLow
    Double previousDayClose
    Double priceChange
    Float priceChangePercentage
    Integer volume
    Double dividend
    Double splitCoefficient

    /**
     * Last market date presented as YYYY-MM-dd
     * Incoming quotes with the same market date will overwrite existing quotes
     */
    @Temporal(TemporalType.DATE)
    Date quoteDate

}
