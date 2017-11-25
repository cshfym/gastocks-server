package com.gastocks.server.models.domain

import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

/**
 * Domain object compatible with any quote
 */
@ToString
@Entity (name="sector_performance")
class PersistableSectorPerformance {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    @ManyToOne
    @JoinColumn(name = "sector_id")
    PersistableSector sector

    double price
    double dayOpen
    double dayHigh
    double dayLow
    int volume

    /**
     * Last market date presented as YYYY-MM-dd
     * Incoming quotes with the same market date will overwrite existing quotes
     */
    @Temporal(TemporalType.DATE)
    Date quoteDate

    @Override
    String toString() {
        "PersistableSectorPerformance [${sector.description}] quoteDate: ${quoteDate}, price: ${price}, volume: ${volume}"
    }
}
