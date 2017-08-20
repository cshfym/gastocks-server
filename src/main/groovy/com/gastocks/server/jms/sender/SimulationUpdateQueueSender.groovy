package com.gastocks.server.jms.sender

import com.gastocks.server.models.domain.jms.QueueableSimulationCountUpdate
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class SimulationUpdateQueueSender {

    @Autowired
    ApplicationContext applicationContext

    /* Queue for simulation count update requests */
    final static String QUEUE_SIMULATION_UPDATE_PROCESSED_SYMBOLS = "com.gastocks.queue.simulation.update.processed.symbols"

    /**
     * Queues a simulation update request for processing
     * NOTE: THIS QUEUE IS DESIGNED TO BE SINGLE-THREADED, AS IT HANDLES UPDATES TO THE SIMULATION OBJECT
     * @return void
     */
    void queueSimulationCountUpdate(String simulationId, int count) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        def queueAction = new QueueableSimulationCountUpdate(
            simulationId: simulationId,
            count: count
        )

        log.info "Queueing a simulation count update for processing: [${simulationId}] with count [${count}]"
        jmsTemplate.convertAndSend(QUEUE_SIMULATION_UPDATE_PROCESSED_SYMBOLS, queueAction)
    }

}
