package com.gastocks.server.jms.sender

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class IntrinioCompanyQueueSender {

    @Autowired
    ApplicationContext applicationContext

    /* Queue for company dump requests */
    final static String QUEUE_COMPANY_DUMP_REQUEST = "com.gastocks.queue.company.dump"

    /**
     * Queues a company dump
     */
    void queueRequest(String identifier) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing a company dump request for processing: [${identifier}]"

        jmsTemplate.convertAndSend(QUEUE_COMPANY_DUMP_REQUEST, identifier)
    }

}
