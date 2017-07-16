package com.gastocks.server.util

import com.gastocks.server.models.domain.PersistableExchangeMarket
import com.gastocks.server.services.domain.HolidayCalendarPersistenceService
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class DateUtility {

    @Autowired
    HolidayCalendarPersistenceService holidayCalendarPersistenceService

    final static DateTimeFormatter SHORT_DATE_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd")
    final static int MAX_DAYS_BACK = 365


    List<String> buildChronologicalDateListNoWeekends(PersistableExchangeMarket exchangeMarket, DateTime startDate, DateTime endDate = null) {

        List<String> dateList = []
        int daysBack = 1

        addDateToCollectionConditionally(dateList, startDate, exchangeMarket)

        while(true) {
            DateTime previousDay = startDate.minusDays(daysBack)
            if ((daysBack == MAX_DAYS_BACK) || (previousDay < endDate)) {
                break
            }
            addDateToCollectionConditionally(dateList, previousDay, exchangeMarket)
            daysBack++
        }

        dateList
    }

    void addDateToCollectionConditionally(List<String> dateList, DateTime date, PersistableExchangeMarket exchangeMarket) {

        if (isDateOnWeekend(date)) { return }

        if (holidayCalendarPersistenceService.findByExchangeMarketAndHolidayDate(exchangeMarket, new Date(date.millis))) {
            return
        }

        dateList << date.toString(SHORT_DATE_FORMAT)
    }

    boolean isDateOnWeekend(DateTime date) {
        (Integer.valueOf(date.dayOfWeek().toString()) == DateTimeConstants.SATURDAY) ||
                (Integer.valueOf(date.dayOfWeek().toString()) == DateTimeConstants.SUNDAY)
    }

    boolean isDateBeforeToday(DateTime date) {
        def today = new LocalDate()
        def localDate = new LocalDate(date.millis)
        localDate.isBefore(today)
    }
}
