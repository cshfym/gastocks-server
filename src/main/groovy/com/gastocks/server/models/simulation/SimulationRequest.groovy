package com.gastocks.server.models.simulation

import java.beans.Transient

class SimulationRequest {

    String description
    double commissionPrice
    int shares
    List<String> symbols

    MACDRequestParameters macdParameters

    @Transient
    @Override
    String toString() {
        "Simulation request: [${description}], MACD: Short [${macdParameters.macdShortPeriod}, Long [${macdParameters.macdLongPeriod}], Centerline Trigger: [${macdParameters.macdPositiveTrigger}]]"
    }
}
