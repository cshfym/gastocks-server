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

    /* Queue for audit requests */
    final static String QUEUE_QUOTE_AUDIT = "com.gastocks.queue.quote.audit"

    /* Queue for audit reload/correction requests */
    final static String QUEUE_QUOTE_AUDIT_RELOAD = "com.gastocks.queue.quote.audit.reload"

    /**
     * Queues a symbol for quote audit processing
     * @param identifier
     */
    void queueAuditRequest(String identifier) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing an symbol for quote audit processing: [${identifier}]"

        jmsTemplate.convertAndSend(QUEUE_QUOTE_AUDIT, identifier)
    }

    /**
     * Queues a symbol for quote audit correction/reload processing
     * @param identifier
     */
    void queueAuditReloadRequest(String auditId) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing a quote audit correction/reload processing with id [${auditId}]"

        jmsTemplate.convertAndSend(QUEUE_QUOTE_AUDIT_RELOAD, auditId)
    }
}
