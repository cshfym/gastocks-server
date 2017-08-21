package com.gastocks.server.services.simulation

import com.gastocks.server.jms.sender.SimulationQueueSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.domain.PersistableSimulation
import com.gastocks.server.models.exception.SimulationNotFoundException
import com.gastocks.server.models.simulation.SimulationRequest
import com.gastocks.server.models.simulation.SimulationSummary
import com.gastocks.server.models.simulation.SymbolSimulationSummary
import com.gastocks.server.models.simulation.SimulationTransaction
import com.gastocks.server.models.symbol.Symbol
import com.gastocks.server.services.SymbolService
import com.gastocks.server.services.domain.SimulationPersistenceService
import com.gastocks.server.services.domain.SimulationTransactionPersistenceService
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Slf4j
@Service
@CompileStatic
class SimulationApiService {

    @Autowired
    SimulationQueueSender simulationQueueSender

    @Autowired
    SimulationPersistenceService simulationPersistenceService

    @Autowired
    SimulationTransactionPersistenceService simulationTransactionPersistenceService

    @Autowired
    SymbolService symbolService

    BasicResponse queueSimulationRequest(SimulationRequest request) {

        // 1. Load all symbols, pare down if request parameters have specific symbols to simulate.
        List<Symbol> allSymbols = symbolService.findAllSymbols()

        List<Symbol> filteredSymbols = []
        allSymbols.each { symbol ->
            if (request.symbols) {
                if (request.symbols.contains(symbol.identifier)) {
                    filteredSymbols << symbol
                }
            } else {
                filteredSymbols << symbol
            }
        }

        // 2. Build and persist a Simulation record.
        PersistableSimulation simulation

        try {
            simulation = simulationPersistenceService.persistNewSimulation(request.description, filteredSymbols.size(), JsonOutput.toJson(request))
        } catch (Exception ex) {
            log.error(ex.message)
            return new BasicResponse(success: false, message: "Could not persist simulation, exception: ${ex.message}")
        }

        Thread.sleep(5000) // Pause for (hopefully) the parent simulation to persist and commit before firing off the queue transactions.

        // 3. Queue filtered symbols for simulation, linked to Simulation record so they can be linked.
        filteredSymbols.each { symbol ->
            simulationQueueSender.queueRequest(simulation.id, symbol.identifier)
        }

        new BasicResponse(success: true, message: "Queued simulation [${simulation.id}]")
    }

    @Cacheable(value="getSimulationSummaryById")
    SimulationSummary getSimulationSummaryById(String id) {

        def startStopwatch = System.currentTimeMillis()

        PersistableSimulation simulation = getSimulationById(id)

        // Group all simulation transactions by symbol
        def symbolTransactionMap = new HashMap<String,List<SimulationTransaction>>()

        def simulationTransactions = simulationTransactionPersistenceService.findAllBySimulation(simulation)

        simulationTransactions?.each { transaction ->
            List<SimulationTransaction> mappedTransactions = []
            if (symbolTransactionMap.containsKey(transaction.symbol.identifier)) {
                mappedTransactions = symbolTransactionMap.get(transaction.symbol.identifier)
            }
            mappedTransactions << new SimulationTransaction(transaction.commission, transaction.purchaseDate, transaction.purchasePrice,
                    transaction.sellDate, transaction.sellPrice, transaction.symbol.identifier, transaction.shares)

            symbolTransactionMap.put(transaction.symbol.identifier, mappedTransactions)
        }

        def symbolSimulationSummaryList = []
        symbolTransactionMap.each { k, v ->
            symbolSimulationSummaryList << new SymbolSimulationSummary(k, v)
        }

        log.debug ("Returning simulation summary for ID [${id}] in [${System.currentTimeMillis() - startStopwatch} ms]")

        new SimulationSummary(symbolSimulationSummaryList)
    }

    PersistableSimulation getSimulationById(String id) {

        def simulation = simulationPersistenceService.findById(id)
        if (!simulation) {
            throw new SimulationNotFoundException(identifier: id)
        }

        simulation
    }

    List<PersistableSimulation> findAll() {
        def simulations = simulationPersistenceService.findAll()
        simulations.sort { s1, s2 -> s2.runDate <=> s1.runDate }
    }
}
