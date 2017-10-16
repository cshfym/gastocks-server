package com.gastocks.server.converters

import com.gastocks.server.models.IQuote
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

abstract class BaseConverter implements IConverter {

    abstract boolean hasData(Object obj)

    abstract <T extends IQuote> T fromObject(Object obj)

    static double parseToDouble(String val) {
        try {
            Double.parseDouble(val)
        } catch (NumberFormatException ex) {
            throw ex
        }
    }

    static double parseToFloat(String val) {
        try {
            Float.parseFloat(val)
        } catch (NumberFormatException ex) {
            throw ex
        }
    }

    static int parseToInteger(String val) {
        try {
            Integer.parseInt(val)
        } catch (NumberFormatException ex) {
            throw ex
        }
    }

}
