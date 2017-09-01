package com.gastocks.server.jms.sender

import com.gastocks.server.models.domain.jms.QueueableSimulationSymbol
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
     * @param simulationId, symbol
     */
    void queueRequest(String simulationId, String symbol) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        def queuableSimulationSymbol = new QueueableSimulationSymbol(
            simulationId: simulationId,
            symbol: symbol
        )

        log.info "Queueing a simulation request for processing: [${symbol}]"

        jmsTemplate.convertAndSend(QUEUE_SIMULATION_REQUEST, queuableSimulationSymbol)
    }

}
