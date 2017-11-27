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

    static final String ACTIVE_SYMBOLS = "activeSymbols"
    static final String ALL_HOLIDAY_CALENDARS = "allHolidayCalendars"
    static final String ALL_SYMBOLS = "allSymbols"
    static final String GET_QUOTES_FOR_SYMBOL = "getQuotesForSymbol"
    static final String GET_TECHNICAL_QUOTES_FOR_SYMBOL = "getTechnicalQuotesForSymbol"
    static final String GET_TECHNICAL_QUOTE_FOR_SECTOR = "getTechnicalQuoteForSector"
    static final String FIND_ALL_QUOTES_FOR_SYMBOL = "findAllQuotesForSymbol"
    static final String FIND_ALL_COMPANIES_BY_SECTOR = "findAllCompaniesBySector"
    static final String GET_SIMULATION_SUMMARY_BY_ID = "getSimulationSummaryById"
    static final String VIEW_SYMBOL_EXTENDED = "viewSymbolExtended"
    static final String ALL_SECTORS = "allSectors"
    static final String SECTOR_BY_SYMBOL_IDENTIFIER = "findSectorBySymbol"
    static final String ALL_INDUSTRIES = "allIndustries"

    @Bean
    static CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                ACTIVE_SYMBOLS,
                ALL_HOLIDAY_CALENDARS,
                ALL_SYMBOLS,
                GET_QUOTES_FOR_SYMBOL,
                GET_TECHNICAL_QUOTES_FOR_SYMBOL,
                GET_TECHNICAL_QUOTE_FOR_SECTOR,
                FIND_ALL_QUOTES_FOR_SYMBOL,
                FIND_ALL_COMPANIES_BY_SECTOR,
                GET_SIMULATION_SUMMARY_BY_ID,
                VIEW_SYMBOL_EXTENDED,
                SECTOR_BY_SYMBOL_IDENTIFIER,
                ALL_SECTORS,
                ALL_INDUSTRIES
        )
    }

    @CacheEvict(allEntries = true, value = "viewSymbolExtended")
    @Scheduled(fixedDelay = 43200000L, initialDelay = 43200000L) // Every 12 hours.
    static void reportCacheEvict() {
        log.info "Flushing cache [${VIEW_SYMBOL_EXTENDED}] at [${new Date().toString()}]"
    }

}
