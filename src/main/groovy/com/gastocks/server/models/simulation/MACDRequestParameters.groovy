package com.gastocks.server.models.simulation

/**
 * Class to hold incoming request MACD request parameters for simulations.
 */
class MACDRequestParameters {

    int macdShortPeriod
    int macdLongPeriod
    boolean macdPositiveTrigger // Only allow affirmative BUY signal for MACD when the MACD is above 0.0

}
