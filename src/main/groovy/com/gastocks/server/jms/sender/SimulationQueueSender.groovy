package com.gastocks.server.jms.sender

import com.gastocks.server.models.simulation.SimulationRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class SimulationQueueSender {

    @Autowired
    ApplicationContext applicationContext

    /* Queue for simulation requests */
    final static String QUEUE_SIMULATION_REQUEST = "com.gastocks.queue.simulation"

    /**
     * Queues a simulation request for processing
     * @param @SimulationRequest
     * @return
     */
    void queueRequest(SimulationRequest request) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing a simulation request for processing: ${request}"
        jmsTemplate.convertAndSend(QUEUE_SIMULATION_REQUEST, request)
    }

}
