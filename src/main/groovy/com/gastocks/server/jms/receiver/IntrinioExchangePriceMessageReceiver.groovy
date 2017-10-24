package com.gastocks.server.jms.receiver

import com.gastocks.server.jms.sender.IntrinioExchangeQueueSender
import com.gastocks.server.models.intrinio.IntrinioExchangeRequest
import com.gastocks.server.services.intrinio.exchangeprices.ExchangePriceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class IntrinioExchangePriceMessageReceiver {

    @Autowired
    ExchangePriceService exchangePriceService

    @JmsListener(destination = IntrinioExchangeQueueSender.QUEUE_EXCHANGE_PRICE_REQUEST, containerFactory = "intrinioExchangePriceFactory")
    void receiveExchangePriceMessage(IntrinioExchangeRequest request) {

        log.debug "Received [${request}] from queue ${IntrinioExchangeQueueSender.QUEUE_EXCHANGE_PRICE_REQUEST}"

        exchangePriceService.fetchAndPersistExchangePrices(request)
    }
}
