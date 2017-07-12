package com.gastocks.server.jms.receiver

import com.gastocks.server.models.domain.jms.QueueableQuote
import groovy.util.logging.Slf4j
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class Receiver {

    @JmsListener(destination = "quote_queue", containerFactory = "quoteFactory")
    void receiveMessage(QueueableQuote quote) {
        log.info "Received <{ ${quote} }> in quote_queue"
    }
}
