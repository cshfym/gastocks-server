package com.gastocks.server.jms.receiver

import com.gastocks.server.jms.sender.SimulationUpdateQueueSender
import com.gastocks.server.jms.services.simulation.SimulationService
import com.gastocks.server.models.domain.jms.QueueableSimulationCountUpdate
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class SimulationUpdateCountMessageReceiver {

    @Autowired
    SimulationService simulationService

    @JmsListener(destination = SimulationUpdateQueueSender.QUEUE_SIMULATION_UPDATE_PROCESSED_SYMBOLS, containerFactory = "simulationUpdateCountFactory")
    void receiveSimulationUpdateCountRequest(QueueableSimulationCountUpdate simulationCountUpdate) {

        log.debug "Received [${simulationCountUpdate.simulationId}] from queue ${SimulationUpdateQueueSender.QUEUE_SIMULATION_UPDATE_PROCESSED_SYMBOLS}"

        simulationService.doSimulationUpdateCount(simulationCountUpdate)
    }
}
