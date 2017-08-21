package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableSimulation
import org.springframework.data.repository.CrudRepository

interface SimulationRepository extends CrudRepository<PersistableSimulation, String> {

}