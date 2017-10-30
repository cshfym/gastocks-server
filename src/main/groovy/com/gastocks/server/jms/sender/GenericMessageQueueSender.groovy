package com.gastocks.server.jms.sender

import com.gastocks.server.jms.models.GenericIdServiceMessage
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class GenericMessageQueueSender {

    @Autowired
    ApplicationContext applicationContext

    /* Queue for company dump requests */
    final static String QUEUE_GENERIC_ID_MESSAGE = "com.gastocks.queue.generic.id.message"

    /**
     * Queues a generic ID service executor request
     */
    void queueRequest(GenericIdServiceMessage serviceMessage) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing a generic ID service executor request for processing: [${serviceMessage.identifier}] in [${serviceMessage.serviceClass.name}]"

        jmsTemplate.convertAndSend(QUEUE_GENERIC_ID_MESSAGE, serviceMessage)
    }

}
