package com.gastocks.server.models.simulation

/**
 * Contains the collection of all simulation transactions for a given symbol
 */
class SymbolSimulationSummary {

    // Base attributes
    String symbol
    List<SimulationTransaction> transactions

    // Calculated properties
    double totalInvestment
    double netProceeds
    double netProceedsPercentage
    double grossProceeds
    double grossProceedsPercentage
    double totalCommissionCost
    int transactionCount

    SymbolSimulationSummary(String symbol, List<SimulationTransaction> transactions = []) {

        this.symbol = symbol
        this.transactions = transactions

        this.transactionCount = transactions.size()
        this.totalInvestment = ((double)transactions.totalInvestment.sum()).round(2)
        this.netProceeds = ((double)transactions.netProceeds.sum()).round(2)
        this.grossProceeds = ((double)transactions.grossProceeds.sum()).round(2)
        this.netProceedsPercentage = ((double)(netProceeds / totalInvestment) * 100.0d).round(2)
        this.grossProceedsPercentage = ((double)(grossProceeds / totalInvestment) * 100.0d).round(2)
        this.totalCommissionCost = transactions.commission.sum() as double
    }

}
