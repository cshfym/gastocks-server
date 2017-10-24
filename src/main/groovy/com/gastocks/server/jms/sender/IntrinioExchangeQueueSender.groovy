package com.gastocks.server.jms.sender

import com.gastocks.server.models.intrinio.IntrinioExchangeRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class IntrinioExchangeQueueSender {

    @Autowired
    ApplicationContext applicationContext

    /* Queue for company dump requests */
    final static String QUEUE_EXCHANGE_PRICE_REQUEST = "com.gastocks.queue.exchange.price.request"

    /**
     * Queues a company dump
     */
    void queueRequest(IntrinioExchangeRequest request) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing an exchange price request for processing: [${request}]"

        jmsTemplate.convertAndSend(QUEUE_EXCHANGE_PRICE_REQUEST, request)
    }

}
