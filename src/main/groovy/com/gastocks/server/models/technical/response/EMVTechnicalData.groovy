package com.gastocks.server.models.technical.response

/**
 * High volume + modest price range = difficult movement
 * Low volume + large price range = easy movement
 * In general:
 *   Prices are rising with relative ease when the oscillator is in positive territory
 *   Prices are falling with relative ease when the oscillator is in negative territory
 *
 */
class EMVTechnicalData {

    // Periods is the number of trading periods used to calculate the simple moving average (smoothed) EMV
    int periods

    double boxRatio // Typically 100,000,000 - depends on volume.

    int volume

    double easeOfMovement
    double easeOfMovementSMA

    boolean smaCrossoverPositive
    boolean smaCrossoverNegative

    /**
     * "Easy movement" is low volume and a large price range*
     */
    boolean easyMovement

    /**
     * "Difficult movement is high volume and a small price range
     */
    boolean difficultMovement

    /**
     * Volume that exceeds this percentage of the period average, combined with high-low price lower than the highLowTriggerPercentage will trigger a "difficult" movement indicator
     */
    double volumeTriggerPercentage

    /**
     * High-low price that exceeds this percentage of the period average, combined with volume that exceeds the volumeTriggerPercentage will trigger an "easy" movement indicator
     */
    double highLowTriggerPercentage
}
