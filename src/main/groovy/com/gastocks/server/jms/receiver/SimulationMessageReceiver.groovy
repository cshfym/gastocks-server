package com.gastocks.server.jms.receiver

import com.gastocks.server.jms.sender.SimulationQueueSender
import com.gastocks.server.jms.services.simulation.SimulationService
import com.gastocks.server.models.simulation.SimulationRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class SimulationMessageReceiver {

    @Autowired
    SimulationService simulationService

    @JmsListener(destination = SimulationQueueSender.QUEUE_SIMULATION_REQUEST, containerFactory = "simulationFactory")
    void receiveSimulationRequest(SimulationRequest request) {

        log.info "Received [${request}] from queue ${SimulationQueueSender.QUEUE_SIMULATION_REQUEST}"

        simulationService.doSimulationWithRequest(request)
    }
}
