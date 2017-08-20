package com.gastocks.server.models.domain

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity(name="simulation")
class PersistableSimulation {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    String description

    @Temporal(TemporalType.DATE)
    Date runDate

    @Type(type="text")
    String attributes

    /**
     * The number of symbols queued as part of this simulation.
     */
    int queuedSymbols

    /**
     * The number of symbols which have completed simulation.
     */
    int processedSymbols

    @Override
    String toString() {
        "PersistableSimulation [${description}]"
    }
}
