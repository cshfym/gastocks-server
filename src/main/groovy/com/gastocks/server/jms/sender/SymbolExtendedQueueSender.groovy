package com.gastocks.server.jms.sender

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class SymbolExtendedQueueSender {

    @Autowired
    ApplicationContext applicationContext

    /* Queue for simulation requests */
    final static String QUEUE_SYMBOL_EXTENDED_BACKFILL = "com.gastocks.queue.symbol.extended.backfill"

    /**
     * Queues an extended symbol backfill request for processing
     * @param @SimulationRequest
     * @return
     */
    void queueRequest(String identifier) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing an extended symbol backfill request for processing: [${identifier}]"

        jmsTemplate.convertAndSend(QUEUE_SYMBOL_EXTENDED_BACKFILL, identifier)
    }

}
