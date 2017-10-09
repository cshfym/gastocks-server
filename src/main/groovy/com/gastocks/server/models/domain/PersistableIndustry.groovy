package com.gastocks.server.models.domain

import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

/**
 * Domain object for industry
 */
@ToString
@Entity (name="industry")
class PersistableIndustry {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    String description

    @Override
    String toString() {
        "PersistableIndustry [${description}]"
    }
}
