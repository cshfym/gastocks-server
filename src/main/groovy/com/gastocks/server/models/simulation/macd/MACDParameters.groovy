package com.gastocks.server.models.simulation.macd

class MACDParameters {

    int macdShortPeriod
    int macdLongPeriod
    boolean macdPositiveTrigger // Only allow affirmative BUY signal for MACD when the MACD is above 0.0

}
