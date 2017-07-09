package com.gastocks.server.converters

class ConverterUtils {

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
