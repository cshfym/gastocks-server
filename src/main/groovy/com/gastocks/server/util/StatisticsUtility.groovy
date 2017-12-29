package com.gastocks.server.util

import org.springframework.stereotype.Component

@Component
class StatisticsUtility {

    static double getMean(List data) {

        double sum = 0.0
        data.each {
            sum += it
        }

        (data) ? sum / data.size() : 0.0d
    }

    static double getVariance(List data) {

        double mean = getMean(data)
        double temp = 0
        data.each {
            temp += (it - mean) * (it - mean)
        }

        (data) ? (double)(temp / (data.size() - 1)).round(2) : 0.0d
    }

    static double getStandardDeviation(List data) {
        def standardDeviation = Math.sqrt(getVariance(data))
        if (!standardDeviation || Double.isNaN(standardDeviation)) {
            return 0.0d
        }
        standardDeviation
    }

}
