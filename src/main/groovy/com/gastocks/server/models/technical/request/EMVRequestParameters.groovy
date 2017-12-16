package com.gastocks.server.models.technical.request

/**
 * Class to hold incoming request EMV request parameters for simulations.
 */
class EMVRequestParameters {

    /**
     * Simple moving average number of smoothing periods
     */
    int periods

    /**
     * Typically 100,000,000 - higher values reduce ratio. Helps smooth EMV.
     */
    double boxRatio

    /**
     * Volume that exceeds this percentage of the period average, combined with high-low price lower than the highLowTriggerPercentage will trigger a "difficult" movement indicator
     */
    double volumeTriggerPercentage = 1.25

    /**
     * High-low price that exceeds this percentage of the period average, combined with volume that exceeds the volumeTriggerPercentage will trigger an "easy" movement indicator
     */
    double highLowTriggerPercentage = 1.50
}
