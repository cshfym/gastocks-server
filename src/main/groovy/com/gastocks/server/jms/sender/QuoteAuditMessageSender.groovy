package com.gastocks.server.jms.sender

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class QuoteAuditMessageSender {

    @Autowired
    ApplicationContext applicationContext

    /* Queue for simulation requests */
    final static String QUEUE_QUOTE_AUDIT_BACKFILL = "com.gastocks.queue.quote.audit"

    /**
     * Queues a symbol for quote audit processing
     * @param identifier
     */
    void queueRequest(String identifier) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing an symbol for quote audit processing: [${identifier}]"

        jmsTemplate.convertAndSend(QUEUE_QUOTE_AUDIT_BACKFILL, identifier)
    }

}
