package com.gastocks.server.converters

import com.gastocks.server.models.IQuote
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

abstract class BaseConverter implements IConverter {

    DateTimeFormatter shortDateformat = DateTimeFormat.forPattern("YYYY-MM-dd")
    DateTimeFormatter longDateFormat = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss")

    abstract boolean hasData(Object obj)

    abstract <T extends IQuote> T fromObject(Object obj)

    static Double parseToDouble(String val) {
        try {
            Double.parseDouble(val)
        } catch (Exception ex) {
            null
        }
    }

    static Double parseToFloat(String val) {
        try {
            Float.parseFloat(val)
        } catch (Exception ex) {
            null
        }
    }

    static Integer parseToInteger(String val) {
        try {
            Integer.parseInt(val)
        } catch (Exception ex) {
            null
        }
    }

    DateTime parseDateString(String dateString) {

        DateTime dateTime

        try {
            dateTime = DateTime.parse(dateString, shortDateformat)
        } catch (Exception ex) {
            // Swallow
        }

        if (dateTime) { return dateTime }

        try {
            dateTime = DateTime.parse(dateString, longDateFormat)
        } catch (Exception ex) {
            // Swallow
        }

        dateTime
    }
}
