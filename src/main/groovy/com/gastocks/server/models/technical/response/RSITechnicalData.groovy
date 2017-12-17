package com.gastocks.server.models.technical.response

class RSITechnicalData {

    /**
     * Interval is the number of data points back for calculating the RSI, with a default of 14.
     */
    int interval

    int overBoughtLine
    int overSoldLine

    int periodsOverBought
    int periodsOverSold

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

    boolean overBought
    boolean overBoughtCrossoverNegative     // RSI value crosses below overbought line
    boolean overBoughtCrossoverPositive     // RSI value crosses above overbought line

    boolean overSold
    boolean overSoldCrossoverNegative       // RSI value crosses below oversold line
    boolean overSoldCrossoverPositive       // RSI value crosses above oversold line

    boolean centerLineCrossoverNegative     // RSI value crosses below 50 line
    boolean centerLineCrossoverPositive     // RSI value crosses above 50 line

}
