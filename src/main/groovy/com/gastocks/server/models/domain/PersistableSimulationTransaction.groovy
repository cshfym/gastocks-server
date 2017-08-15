package com.gastocks.server.models.domain

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name="simulation_transaction")
class PersistableSimulationTransaction {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    @ManyToOne
    @JoinColumn(name = "simulation_id")
    PersistableSimulation simulation

    @ManyToOne
    @JoinColumn(name = "symbol_id")
    PersistableSymbol symbol

    double commission
    Date purchaseDate
    double purchasePrice
    Date sellDate
    double sellPrice
    int shares

    Date createDate

}
