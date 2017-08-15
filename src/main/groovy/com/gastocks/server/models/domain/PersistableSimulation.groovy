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
    Date runDate

    @Type(type="text")
    String attributes

    @Override
    String toString() {
        "PersistableSimulation [${description}]"
    }
}
