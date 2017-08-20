package com.gastocks.server.resources

import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.domain.PersistableSimulation
import com.gastocks.server.models.domain.PersistableSimulationTransaction
import com.gastocks.server.models.exception.SimulationNotFoundException
import com.gastocks.server.models.simulation.SimulationRequest
import com.gastocks.server.models.simulation.SimulationSummary
import com.gastocks.server.services.simulation.SimulationApiService
import com.gastocks.server.services.simulation.SimulationTransactionApiService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/simulation")
class SimulationResource {

    @Autowired
    SimulationApiService simulationApiService

    @Autowired
    SimulationTransactionApiService simulationTransactionApiService

    @ResponseBody
    @RequestMapping(method=RequestMethod.POST)
    BasicResponse doBasicSimulation(@RequestBody SimulationRequest request) {
        simulationApiService.queueSimulationRequest(request)
    }

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    List<PersistableSimulation> findAllSimulations() {
        simulationApiService.findAll()
    }

    @ResponseBody
    @RequestMapping(value="/{simulationId}/{symbol}/transactions", method=RequestMethod.GET)
    List<PersistableSimulationTransaction> findAllTransactionsBySimulationId(@PathVariable("simulationId") String simulationId,
        @PathVariable("symbol")String symbol) {

        def transactions

        try {
            transactions = simulationTransactionApiService.findAllBySimulationIdAndSymbolId(simulationId, symbol)
        } catch (SimulationNotFoundException ex) {
            log.info("Simulation not found by id [${simulationId}]")
            throw ex
        }

        transactions
    }

    @ResponseBody
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    PersistableSimulation getSimulationById(@PathVariable("id") String id) {

        try {
            simulationApiService.getSimulationById(id)
        } catch (SimulationNotFoundException ex) {
            log.info("Simulation not found by id [${id}]")
            throw ex
        }
    }

    @ResponseBody
    @RequestMapping(value="/{id}/summary", method=RequestMethod.GET)
    SimulationSummary getSimulationSummaryBySimulationId(@PathVariable("id") String id) {

        try {
            simulationApiService.getSimulationSummaryById(id)
        } catch (SimulationNotFoundException ex) {
            log.info("Simulation not found by id [${id}]")
            throw ex
        }
    }

}