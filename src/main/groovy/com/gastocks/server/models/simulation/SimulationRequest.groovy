package com.gastocks.server.models.simulation

import com.gastocks.server.models.simulation.macd.MACDParameters

class SimulationRequest {

    String description
    double commissionPrice

    MACDParameters macdParameters

    @Override
    String toString() {
        "Simulation request: [${description}], MACD: Short [${macdParameters.macdShortPeriod}, Long [${macdParameters.macdLongPeriod}], Centerline Trigger: [${macdParameters.macdPositiveTrigger}]]"
    }
}
