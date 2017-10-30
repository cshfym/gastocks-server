package com.gastocks.server.util

import org.springframework.stereotype.Component

@Component
class NumberUtility {

    static double safeProcessDouble(double val) {

        try {
            Double.parseDouble(val?.toString())
        } catch (Exception ex) {
            return 0.0d
        }

        val
    }

    static double safeProcessDouble(String val) {

        if (!val) { return 0 }

        try {
            Double.parseDouble(val?.toString())
        } catch (Exception ex) {
            0.0d
        }
    }

    static int safeProcessInteger(int val) {

        try {
            Integer.parseInt(val?.toString())
        } catch (Exception ex) {
            return 0
        }

        val
    }

    static int safeProcessInteger(String val) {

        if (!val) { return 0 }

        try {
            Integer.parseInt(val)
        } catch (Exception ex) {
            0
        }

    }

}
