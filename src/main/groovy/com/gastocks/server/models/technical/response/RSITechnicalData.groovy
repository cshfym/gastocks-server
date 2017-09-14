package com.gastocks.server.models.technical.response

class RSITechnicalData {

    /**
     * Interval is the number of data points back for calculating the RSI, with a default of 14.
     */
    int interval

    int overBoughtLine
    int overSoldLine

    double priceGain
    double priceLoss

    double averagePriceGain
    double averagePriceLoss

    /**
     * Relative strength is the averagePriceGain / averagePriceLoss
     */
    double relativeStrength

    /**
     * Relative strength index is calculated as "100 - (100 / (1 + relativeStrength))"
     */
    double relativeStrengthIndex
}
