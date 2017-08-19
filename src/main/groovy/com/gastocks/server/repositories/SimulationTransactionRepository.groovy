package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableSimulation
import com.gastocks.server.models.domain.PersistableSimulationTransaction
import com.gastocks.server.models.domain.PersistableSymbol
import org.springframework.data.repository.CrudRepository

interface SimulationTransactionRepository extends CrudRepository<PersistableSimulationTransaction, String> {

    List<PersistableSimulationTransaction> findAllBySimulationAndSymbol(PersistableSimulation simulation, PersistableSymbol symbol)
}