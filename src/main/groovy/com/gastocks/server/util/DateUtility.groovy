package com.gastocks.server.util

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.stereotype.Component

@Component
class DateUtility {

    final static DateTimeFormatter SHORT_DATE_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd")
    final static int MAX_DAYS_BACK = 365

    List<String> buildChronologicalDateList(DateTime startDate, DateTime endDate = null) {

        List<String> dateList = []
        int daysBack = 1

        dateList << startDate.toString(SHORT_DATE_FORMAT)

        while(true) {
            DateTime previousDay = startDate.minusDays(daysBack)
            if ((daysBack == MAX_DAYS_BACK) || (previousDay < endDate)) {
                break
            }
            dateList << previousDay.toString(SHORT_DATE_FORMAT)
            daysBack++
        }

        dateList
    }
}
