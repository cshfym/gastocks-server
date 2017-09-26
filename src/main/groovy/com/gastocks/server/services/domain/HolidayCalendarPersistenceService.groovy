package com.gastocks.server.services.domain

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.models.domain.PersistableExchangeMarket
import com.gastocks.server.models.domain.PersistableHolidayCalendar
import com.gastocks.server.repositories.HolidayCalendarRepository
import groovy.util.logging.Slf4j
import org.joda.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class HolidayCalendarPersistenceService {

    @Autowired
    HolidayCalendarRepository holidayCalendarRepository

    Map<LocalDate,List<String>> exchangeHolidayCalendarMap

    /**
     * Executed on {@PostConstruct} to pre-load the exchange market holiday calendars.
     */
    @PostConstruct
    void initializeHolidayCalendarMap() {

        exchangeHolidayCalendarMap = new HashMap<LocalDate,List<String>>()

        def holidayCalendarDates = findAll()

        holidayCalendarDates.each { holidayCalendar ->
            def supportedMarkets = exchangeHolidayCalendarMap.get(new LocalDate(holidayCalendar.holidayDate))
            if (supportedMarkets) {
                supportedMarkets << holidayCalendar.exchangeMarket.shortName
            } else {
                supportedMarkets = [holidayCalendar.exchangeMarket.shortName]
            }

            exchangeHolidayCalendarMap.put(new LocalDate(holidayCalendar.holidayDate), supportedMarkets)
        }
    }

    /**
     * Returns true if the date passed is considered a holiday for the specified market.
     * @param exchangeMarket
     * @param holidayDate
     * @return boolean
     */
    boolean isHolidayDate(PersistableExchangeMarket exchangeMarket, Date holidayDate) {
        def holidayDateEntry = exchangeHolidayCalendarMap.get(new LocalDate(holidayDate))
        if (!holidayDateEntry) { return false }
        if (holidayDateEntry.contains(exchangeMarket.shortName)) { return true }

        false
    }

    @Cacheable(value = CacheConfiguration.ALL_HOLIDAY_CALENDARS)
    List<PersistableHolidayCalendar> findAll() {
        holidayCalendarRepository.findAll()
    }
}
