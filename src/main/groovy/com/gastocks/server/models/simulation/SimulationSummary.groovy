package com.gastocks.server.models.simulation

class SimulationSummary {

    String symbol
    List<String> transactionDetails
    double totalInvestment
    double totalEarningsPercentage
    double netProceeds
    double grossProceeds
    double totalCommissionCost
    int transactionCount

    @Override
    String toString() {
        "Simulation summary for [${symbol}]: Total Investment [${totalInvestment}], Total Earnings Percentage [${totalEarningsPercentage}], " +
                "Gross Proceeds [${netProceeds}], Net Proceeds [${grossProceeds}], " +
            "Total Commission Cost [${totalCommissionCost}], Transaction Count [${transactionCount}]"
    }
}
