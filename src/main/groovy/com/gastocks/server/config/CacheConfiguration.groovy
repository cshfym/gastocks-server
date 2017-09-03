package com.gastocks.server.config

import groovy.util.logging.Slf4j
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Slf4j
@Configuration
@EnableCaching
@EnableScheduling
class CacheConfiguration {

    static final String VIEW_SYMBOL_EXTENDED = "viewSymbolExtended"

    @Bean
    CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "activeSymbols",
                "allHolidayCalendars",
                "allSymbols",
                "getQuotesForSymbol",
                "getTechnicalQuotesForSymbol",
                "QuotePersistenceService.findAllQuotesForSymbol",
                "getSimulationSummaryById",
                VIEW_SYMBOL_EXTENDED
        )
    }

    @CacheEvict(allEntries = true, value = "viewSymbolExtended")
    @Scheduled(fixedDelay = 43200000L, initialDelay = 43200000L) // Every 12 hours.
    void reportCacheEvict() {
        log.info "Flushing cache [${VIEW_SYMBOL_EXTENDED}] at [${new Date().toString()}]"
    }

}
