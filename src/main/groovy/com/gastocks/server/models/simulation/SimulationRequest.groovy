package com.gastocks.server.models.simulation

import com.gastocks.server.models.technical.request.MACDRequestParameters
import com.gastocks.server.models.technical.request.OBVRequestParameters
import com.gastocks.server.models.technical.request.RSIRequestParameters

import java.beans.Transient

class SimulationRequest {

    String description
    double commissionPrice
    int shares
    List<String> symbols

    /**
     * If a stock was purchased but never sold at the end of the simulation, close out the position with the last quote price & date.
     */
    boolean sellOpenPositions

    /**
     * If the stock price remains the same, do not initiate a buy or sell action.
     */
    boolean onlyTransactOnPriceChange

    /**
     * Limit transactions to maxTradingPeriods - helps filter out transactions that span excessive time periods (i.e. > 1 year)
     */
    int maxTradingPeriods

    double minPurchasePrice
    double maxPurchasePrice

    MACDRequestParameters macdParameters

    RSIRequestParameters rsiRequestParameters

    OBVRequestParameters onBalanceVolumeRequestParameters

    @Transient
    @Override
    String toString() {
        "Simulation request: [${description}], MACD: Short [${macdParameters.macdShortPeriod}, Long [${macdParameters.macdLongPeriod}], " +
                "Centerline Trigger: [${macdParameters.macdPositiveTrigger}]]"
    }
}
