package com.gastocks.server.services.simulation

import com.gastocks.server.models.domain.PersistableSimulation
import com.gastocks.server.models.domain.PersistableSimulationTransaction
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.exception.SimulationNotFoundException
import com.gastocks.server.models.exception.SymbolNotFoundException
import com.gastocks.server.services.domain.SimulationPersistenceService
import com.gastocks.server.services.domain.SimulationTransactionPersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
@CompileStatic
@Transactional
class SimulationTransactionApiService {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    SimulationPersistenceService simulationPersistenceService

    @Autowired
    SimulationTransactionPersistenceService simulationTransactionPersistenceService

    List<PersistableSimulationTransaction> findAllBySimulationIdAndSymbolId(String simulationId, String symbolId) {

        PersistableSimulation simulation = simulationPersistenceService.findById(simulationId)
        if (!simulation) {
            throw new SimulationNotFoundException(identifier: simulationId)
        }

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(symbolId)
        if (!symbol) {
            throw new SymbolNotFoundException(identifier: symbolId)
        }

        simulationTransactionPersistenceService.findAllBySimulationAndSymbol(simulation, symbol)
    }
}
