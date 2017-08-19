package com.gastocks.server.models.domain

import org.hibernate.annotations.GenericGenerator

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Temporal
import javax.persistence.TemporalType

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

    @Temporal(TemporalType.DATE)
    Date purchaseDate
    double purchasePrice

    @Temporal(TemporalType.DATE)
    Date sellDate
    double sellPrice

    int shares

    @Temporal(TemporalType.DATE)
    Date createDate

}
