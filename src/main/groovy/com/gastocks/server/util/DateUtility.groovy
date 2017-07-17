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

@Component
class DateUtility {

    final DateTimeFormatter LONG_DATE_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss")
    final DateTimeFormatter SHORT_DATE_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd")
    final static int MAX_DAYS_BACK = 365


    @Autowired
    HolidayCalendarPersistenceService holidayCalendarPersistenceService



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

        String dateToShortString = date.toString(SHORT_DATE_FORMAT)

        if (holidayCalendarPersistenceService.findByExchangeMarketAndHolidayDate(
                exchangeMarket, new Date(SHORT_DATE_FORMAT.parseDateTime(dateToShortString).millis))) {
            return
        }

        dateList << date.toString(SHORT_DATE_FORMAT)
    }

    boolean isDateOnWeekend(DateTime date) {
        (Integer.valueOf(date.dayOfWeek().getAsString()) == DateTimeConstants.SATURDAY) ||
                (Integer.valueOf(date.dayOfWeek().getAsString()) == DateTimeConstants.SUNDAY)
    }

    boolean isDateBeforeToday(DateTime date) {
        def today = new LocalDate()
        def localDate = new LocalDate(date.millis)
        localDate.isBefore(today)
    }

    DateTime parseDateStringDate(String dateString) {

        DateTime dateTime

        try {
            dateTime = DateTime.parse(dateString, SHORT_DATE_FORMAT)
        } catch (Exception ex) {
            // Swallow
        }

        if (dateTime) { return dateTime }

        try {
            dateTime = DateTime.parse(dateString, LONG_DATE_FORMAT)
        } catch (Exception ex) {
            // Swallow
        }

        dateTime
    }
}
