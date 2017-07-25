package com.gastocks.server.schedulers


import com.gastocks.server.services.avglobalquote.AVGlobalQuoteHandlerService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class AVQuoteScheduledTask {

    @Autowired
    AVGlobalQuoteHandlerService quoteHandlerService

    @Scheduled(cron = "0 1 1 * * ?")
    void process() {
        quoteHandlerService.fetchAndQueueAllQuotesForAllSymbols()
    }
}
