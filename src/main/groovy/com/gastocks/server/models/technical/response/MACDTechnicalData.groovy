package com.gastocks.server.models.technical.response

class MACDTechnicalData {

    double emaShort
    double emaLong
    double macd
    double macdSignalLine
    double macdHist

    boolean aboveSignalLine
    boolean belowSignalLine

    boolean centerCrossoverPositive
    boolean centerCrossoverNegative
    boolean signalCrossoverPositive // Crosses over the MACD signal line on a positive uptrend (BUY)
    boolean signalCrossoverNegative // Crosses over the MACD signal line on a negative downtrend (SELL)

    int periodsAboveSignalLine
    int periodsBelowSignalLine
}
