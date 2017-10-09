package com.gastocks.server.jms.receiver

import com.gastocks.server.jms.sender.QuotePriceChangeQueueSender
import com.gastocks.server.services.SymbolService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class QuotePriceChangeMessageReceiver {

    @Autowired
    SymbolService symbolService

    @JmsListener(destination = QuotePriceChangeQueueSender.QUEUE_QUOTE_PRICE_CHANGE_BACKFILL, containerFactory = "quotePriceChangeFillFactory")
    void receiveSymbolExtendedBackfillRequest(String identifier) {

        log.debug "Received [${identifier}] from queue ${QuotePriceChangeQueueSender.QUEUE_QUOTE_PRICE_CHANGE_BACKFILL}"

        symbolService.doBackfillPriceChangeData(identifier)
    }
}
