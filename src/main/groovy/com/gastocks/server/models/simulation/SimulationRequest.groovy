package com.gastocks.server.models.simulation

class SimulationRequest {

    String description
    double commissionPrice

    MACDRequestParameters macdParameters

    @Override
    String toString() {
        "Simulation request: [${description}], MACD: Short [${macdParameters.macdShortPeriod}, Long [${macdParameters.macdLongPeriod}], Centerline Trigger: [${macdParameters.macdPositiveTrigger}]]"
    }
}
