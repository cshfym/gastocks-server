package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableExchangeMarket
import com.gastocks.server.models.domain.PersistableHolidayCalendar
import com.gastocks.server.repositories.HolidayCalendarRepository
import groovy.util.logging.Slf4j
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

    Map<Date,List<PersistableExchangeMarket>> exchangeHolidayCalendar

    @PostConstruct
    void initializeHolidayCalendarMap() {

        exchangeHolidayCalendar = new HashMap<Date,List<PersistableExchangeMarket>>()

        def holidayCalendarDates = findAll()

        holidayCalendarDates.each { holidayCalendar ->
            def supportedMarkets = exchangeHolidayCalendar.get(holidayCalendar.holidayDate)
            if (supportedMarkets) {
                supportedMarkets << holidayCalendar.exchangeMarket
            } else {
                supportedMarkets = [holidayCalendar.exchangeMarket]
            }

            exchangeHolidayCalendar.put(holidayCalendar.holidayDate, supportedMarkets)
        }
    }

    boolean isHolidayDate(PersistableExchangeMarket exchangeMarket, Date holidayDate) {
        def supportedMarketsForDate = exchangeHolidayCalendar.get(holidayDate)
        if (!supportedMarketsForDate) { return false }
        if (supportedMarketsForDate.contains(exchangeMarket)) { return true }

        false
    }

    @Cacheable(value = "allHolidayCalendars")
    List<PersistableHolidayCalendar> findAll() {
        holidayCalendarRepository.findAll()
    }
}
