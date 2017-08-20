package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableSimulation
import com.gastocks.server.repositories.SimulationRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class SimulationPersistenceService {

    @Autowired
    SimulationRepository simulationRepository

    PersistableSimulation findById(String id) {
        simulationRepository.findOne(id)
    }

    List<PersistableSimulation> findAll() {
        simulationRepository.findAll()
    }

    @Transactional
    PersistableSimulation persistNewSimulation(String description, int queuedSymbolsSize, String attributes) {

        def persistableSimulation = new PersistableSimulation(
            description: description,
            queuedSymbols: queuedSymbolsSize,
            attributes: attributes,
            runDate: new Date()
        )

        log.debug("Saving simulation: ${persistableSimulation.toString()}")

        simulationRepository.save(persistableSimulation)
    }

}
