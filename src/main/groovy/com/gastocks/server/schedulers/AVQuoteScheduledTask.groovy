package com.gastocks.server.schedulers


import com.gastocks.server.services.avglobalquote.AVGlobalQuoteHandlerService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class AVQuoteScheduledTask {

    @Autowired
    AVGlobalQuoteHandlerService quoteHandlerService

    @Value('${av.quote.daily.processing.enabled}')
    boolean avGlobalQuoteProcessingEnabled

    // @Scheduled(cron = '${av.quote.daily.schedule}')
    void process() {

        if (!avGlobalQuoteProcessingEnabled) { return }

        quoteHandlerService.fetchAndQueueAllQuotesForAllSymbols()
    }
}
