package com.gastocks.server.converters

import com.gastocks.server.models.IQuote
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

abstract class BaseConverter implements IConverter {

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

}
