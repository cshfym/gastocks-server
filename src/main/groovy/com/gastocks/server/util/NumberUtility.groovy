package com.gastocks.server.util

import org.springframework.stereotype.Component

@Component
class NumberUtility {

    double safeProcessDouble(double val) {

        try {
            Double.parseDouble(val?.toString())
        } catch (Exception ex) {
            return 0.0d
        }

        val
    }

    int safeProcessInteger(int val) {

        try {
            Integer.parseInt(val?.toString())
        } catch (Exception ex) {
            return 0
        }

        val
    }

}
