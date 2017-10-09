package com.gastocks.server.models.domain

import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * Domain object for sector
 */
@ToString
@Entity (name="sector")
class PersistableSector {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    String description

    @Override
    String toString() {
        "PersistableSector [${description}]"
    }
}
