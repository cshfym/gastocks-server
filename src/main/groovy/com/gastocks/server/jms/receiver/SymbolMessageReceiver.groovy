package com.gastocks.server.jms.receiver

import com.gastocks.server.jms.services.avglobalquote.AVGlobalQuoteProcessingService
import com.gastocks.server.jms.services.avtimeseriesadjusted.AVTimeSeriesAdjustedProcessingService
import com.gastocks.server.models.domain.jms.QueueableSymbol
import com.gastocks.server.jms.sender.SymbolQueueSender
import com.gastocks.server.services.avglobalquote.AVGlobalQuoteService
import com.gastocks.server.services.avtimeseriesadjusted.AVTimeSeriesAdjustedQuoteService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class SymbolMessageReceiver {

    @Autowired
    AVTimeSeriesAdjustedQuoteService timeSeriesAdjustedQuoteService

    @Autowired
    AVGlobalQuoteService globalQuoteService

    @Autowired
    AVTimeSeriesAdjustedProcessingService timeSeriesAdjustedProcessingService

    @Autowired
    AVGlobalQuoteProcessingService globalQuoteProcessingService

    @JmsListener(destination = SymbolQueueSender.SYMBOL_QUEUE_DESTINATION_AVTSA, containerFactory = "quoteFactory")
    void receiveAVTSAQueuedSymbol(QueueableSymbol symbol) {

        log.info "Received <{ ${symbol} }> in ${SymbolQueueSender.SYMBOL_QUEUE_DESTINATION_AVTSA}"

        timeSeriesAdjustedProcessingService.processSymbol(symbol, timeSeriesAdjustedQuoteService)
    }

    @JmsListener(destination = SymbolQueueSender.SYMBOL_QUEUE_DESTINATION_AVGQ, containerFactory = "quoteFactory")
    void receiveGQQueuedSymbol(QueueableSymbol symbol) {

        log.info "Received <{ ${symbol} }> in ${SymbolQueueSender.SYMBOL_QUEUE_DESTINATION_AVGQ}"

        globalQuoteProcessingService.processSymbol(symbol, globalQuoteService)
    }
}
