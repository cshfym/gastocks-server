package com.gastocks.server.jms.receiver

import com.gastocks.server.jms.sender.SymbolExtendedQueueSender
import com.gastocks.server.services.SymbolService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class SymbolExtendedMessageReceiver {

    @Autowired
    SymbolService symbolService

    @JmsListener(destination = SymbolExtendedQueueSender.QUEUE_SYMBOL_EXTENDED_BACKFILL, containerFactory = "extendedSymbolFillFactory")
    void receiveSymbolExtendedBackfillRequest(String identifier) {

        log.debug "Received [${identifier}] from queue ${SymbolExtendedQueueSender.QUEUE_SYMBOL_EXTENDED_BACKFILL}"

        symbolService.doBackfillForSymbol(identifier)
    }
}
