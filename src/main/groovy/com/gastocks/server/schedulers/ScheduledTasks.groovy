package com.gastocks.server.schedulers

import com.gastocks.server.models.Quote
import com.gastocks.server.models.Symbol
import com.gastocks.server.repositories.SymbolRepository
import com.gastocks.server.services.QuoteService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import java.text.SimpleDateFormat

@Slf4j
@Component
class ScheduledTasks {

    @Autowired
    SymbolRepository symbolRepository

    @Autowired
    QuoteService quoteService

    //private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class)

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss")

    @Scheduled(fixedRate = 5000L)
    void reportCurrentTime() {
        //log.info("The time is now {}", dateFormat.format(new Date()))

        Symbol agilent = symbolRepository.findByIdentifier("A")

        if (!agilent) {
            log.error("No symbol found for identifier [A]!")
            return
        }

        Quote q = quoteService.getQuote(agilent.identifier)

        log.info("Pulled quote for: \n ${q.toString()}\n")
    }
}
