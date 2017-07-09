package com.gastocks.server.schedulers

import com.gastocks.server.repositories.SymbolRepository
import com.gastocks.server.services.AVGlobalQuoteService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.SimpleDateFormat

@Slf4j
@Component
class ScheduledTasks {

    @Autowired
    SymbolRepository symbolRepository

    @Autowired
    AVGlobalQuoteService quoteService

    //private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class)

    /*
    @Scheduled(fixedRate = 5000L)
    void reportCurrentTime() {
        //log.info("The time is now {}", dateFormat.format(new Date()))

        Symbol agilent = symbolRepository.findByIdentifier("A")

        if (!agilent) {
            log.error("No symbol found for identifier [A]!")
            return
        }

        AVGlobalQuote q = avGlobalQuoteService.getQuote(agilent.identifier)

        log.info("Pulled quote for: \n ${q.toString()}\n")
    }
    */
}
