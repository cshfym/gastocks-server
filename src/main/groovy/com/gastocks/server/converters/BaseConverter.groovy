package com.gastocks.server.converters

import com.gastocks.server.models.IQuote

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
