package com.gastocks.server.services

import com.gastocks.server.services.domain.ViewSymbolExtendedCacheService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class PreFetchService {

    @Value('${data.prefetch.vse}')
    Boolean DATA_PREFETCH_VSE

    @Autowired
    ViewSymbolExtendedCacheService viewSymbolExtendedCacheService

    @Scheduled(fixedDelay = 43200000L, initialDelay = 10000L) // Every 12 hours, delay 10s on startup.
    void preFetchViewSymbolExtended() {

        if (!DATA_PREFETCH_VSE) {
            log.info("Bypassing pre-fetch of viewSymbolExtended. Enable this with the setting `data.prefetch.vse`.")
            return
        }

        log.info("Triggering pre-fetch of viewSymbolExtended")
        viewSymbolExtendedCacheService.findAllViewSymbolExtendedFromCache()
    }

}
