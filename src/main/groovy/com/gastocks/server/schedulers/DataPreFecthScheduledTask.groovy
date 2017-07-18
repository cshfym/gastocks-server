package com.gastocks.server.schedulers

import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Slf4j
@Component
class DataPreFecthScheduledTask {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    //@Scheduled(fixedRate = 600000L)
    // @Scheduled(fixedRate = 6000L)
    void process() {
        symbolPersistenceService.findAllActiveSymbols()
    }

}
