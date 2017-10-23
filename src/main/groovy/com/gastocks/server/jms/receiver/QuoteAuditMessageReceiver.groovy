package com.gastocks.server.jms.receiver

import com.gastocks.server.jms.sender.QuoteAuditMessageSender
import com.gastocks.server.services.QuoteAuditService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class QuoteAuditMessageReceiver {

    @Autowired
    QuoteAuditService quoteAuditService

    @JmsListener(destination = QuoteAuditMessageSender.QUEUE_QUOTE_AUDIT, containerFactory = "quoteAuditFactory")
    void receiveQuoteAuditMessage(String identifier) {

        log.debug "Received [${identifier}] from queue ${QuoteAuditMessageSender.QUEUE_QUOTE_AUDIT}"

        quoteAuditService.runQuoteAuditForIdentifier(identifier)
    }

    @JmsListener(destination = QuoteAuditMessageSender.QUEUE_QUOTE_AUDIT_RELOAD, containerFactory = "quoteAuditReloadFactory")
    void receiveQuoteAuditReloadMessage(String auditId) {

        log.debug "Received [${auditId}] from queue ${QuoteAuditMessageSender.QUEUE_QUOTE_AUDIT_RELOAD}"

        quoteAuditService.doQuoteAuditReload(auditId)
    }
}
