package com.gastocks.server.util

import com.gastocks.server.models.domain.PersistableExchangeMarket
import com.gastocks.server.services.domain.HolidayCalendarPersistenceService
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.DateFormat
import java.text.SimpleDateFormat

@Slf4j
@Component
class DateUtility {

    final DateTimeFormatter LONG_DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
    final DateTimeFormatter SHORT_DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd")
    final static int MAX_DAYS_BACK = 365


    @Autowired
    HolidayCalendarPersistenceService holidayCalendarPersistenceService

    List<String> buildChronologicalDateListNoWeekends(PersistableExchangeMarket exchangeMarket, DateTime startDate, DateTime endDate = null) {

        log.debug("Executed buildChronologicalDateListNoWeekends [${exchangeMarket.shortName}] startDate:[${startDate.toString()}] endDate:[${endDate?.toString()}]")

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

    protected void addDateToCollectionConditionally(List<String> dateList, DateTime date, PersistableExchangeMarket exchangeMarket) {

        if (isDateOnWeekend(date)) { return }

        String dateToShortString = date.toString(SHORT_DATETIME_FORMAT)

        if (holidayCalendarPersistenceService.isHolidayDate(exchangeMarket, new Date(SHORT_DATETIME_FORMAT.parseDateTime(dateToShortString).millis))) {
            return
        }

        dateList << dateToShortString
    }

    static boolean isDateOnWeekend(DateTime date) {
        (Integer.valueOf(date.dayOfWeek().getAsString()) == DateTimeConstants.SATURDAY) ||
                (Integer.valueOf(date.dayOfWeek().getAsString()) == DateTimeConstants.SUNDAY)
    }

    static boolean isDateBeforeToday(DateTime date) {
        def today = new LocalDate()
        def localDate = new LocalDate(date.millis)
        localDate.isBefore(today)
    }

    static Date parseDateString(String value) {

        if (!value) { return null }

        def format = new SimpleDateFormat("yyyy-MM-dd")
        try {
            format.parse(value)
        } catch (Exception ex) {
            log.warn("Could not parse [${value}] as Date object with formatter [${format.toString()}]", ex)
            null
        }
    }

    DateTime parseDateStringDate(String dateString) {

        DateTime dateTime

        try {
            dateTime = DateTime.parse(dateString, SHORT_DATETIME_FORMAT)
        } catch (Exception ex) {
            // Swallow
        }

        if (dateTime) { return dateTime }

        try {
            dateTime = DateTime.parse(dateString, LONG_DATETIME_FORMAT)
        } catch (Exception ex) {
            // Swallow
        }

        dateTime
    }
}
