package com.gastocks.server.models.simulation

import java.beans.Transient

class SimulationRequest {

    String description
    double commissionPrice

    /**
     * If a stock was purchased but never sold at the end of the simulation, close out the position with the last quote price & date.
     */
    boolean sellOpenPositions

    int shares
    List<String> symbols

    double minPurchasePrice
    double maxPurchasePrice

    MACDRequestParameters macdParameters

    @Transient
    @Override
    String toString() {
        "Simulation request: [${description}], MACD: Short [${macdParameters.macdShortPeriod}, Long [${macdParameters.macdLongPeriod}], " +
                "Centerline Trigger: [${macdParameters.macdPositiveTrigger}]]"
    }
}
