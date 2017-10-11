package com.gastocks.server.jms.sender

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.jms.QueueableSymbol
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class SymbolQueueSender {

    @Autowired
    ApplicationContext applicationContext

    /* Time Series Adjusted Quote - Historical Data */
    final static String SYMBOL_QUEUE_DESTINATION_AVTSA = "com.gastocks.queue.symbol.avtsa"

    /* Global Quote - Intra-Day */
    final static String SYMBOL_QUEUE_DESTINATION_AVGQ = "com.gastocks.queue.symbol.avgq"

    /**
     * Pushes a symbol on the quote queue for processing.
     * @param symbol
     * @return
     */
    void queueWithPersistableSymbol(PersistableSymbol symbol, String destination) {

        def queueableSymbol = new QueueableSymbol(symbolId: symbol.id, identifier: symbol.identifier, retryCount: 0)

        queueSymbol(queueableSymbol, destination)
    }

    void queueSymbol(QueueableSymbol queueableSymbol, String destination) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing a symbol for processing: <{ ${queueableSymbol} }>"
        jmsTemplate.convertAndSend(destination, queueableSymbol)
    }
}
