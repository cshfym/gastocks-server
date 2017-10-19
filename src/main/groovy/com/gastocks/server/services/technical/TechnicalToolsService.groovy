package com.gastocks.server.services.technical

import org.springframework.stereotype.Service

@Service
class TechnicalToolsService {

    /**
     * Calculates the exponential moving average for the given price, previous EMA, and days
     * EMA = (Current price - EMA(previous day)) x multiplier + EMA(previous day)
     **/
    static double calculateEMA(double currentValue, double previousPeriodEMA, int days) {
        double multiplier = 2 / (+days + 1)
        double exponentialMovingAverage = (currentValue * multiplier) + (previousPeriodEMA * (1 - multiplier))
        exponentialMovingAverage.round(4)
    }
}
