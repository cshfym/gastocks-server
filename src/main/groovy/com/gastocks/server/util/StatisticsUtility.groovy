package com.gastocks.server.util

import org.springframework.stereotype.Component

@Component
class StatisticsUtility {

    Double getMean(List<Double> data) {

        double sum = 0.0
        data.each {
            sum += it
        }

        (data) ? sum / data.size() : 0.0d
    }

    Double getVariance(List<Double> data) {

        double mean = getMean(data)
        double temp = 0
        data.each {
            temp += (it - mean) * (it - mean)
        }

        (data) ? (double)(temp / (data.size() - 1)).round(2) : 0.0d
    }

    Double getStandardDeviation(List<Double> data) {
        Math.sqrt(getVariance(data)) ?: 0.0d
    }

}
