package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableExchangeMarket
import com.gastocks.server.models.domain.PersistableHolidayCalendar
import com.gastocks.server.repositories.HolidayCalendarRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class HolidayCalendarPersistenceService {

    @Autowired
    HolidayCalendarRepository holidayCalendarRepository

    PersistableHolidayCalendar findByExchangeMarketAndHolidayDate(PersistableExchangeMarket exchangeMarket, Date holidayDate) {
        holidayCalendarRepository.findByExchangeMarketAndHolidayDate(exchangeMarket, holidayDate)
    }

}
