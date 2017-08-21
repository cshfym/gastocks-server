package com.gastocks.server.models.simulation

/**
 * Contains the collection of all simulation transactions for a given symbol
 */
class SimulationSummary {

    // Base attributes
    List<SymbolSimulationSummary> symbolSimulationSummaries

    // Calculated properties
    double totalInvestment
    double netProceeds
    double netProceedsPercentage
    double grossProceeds
    double grossProceedsPercentage
    double totalCommissionCost
    int transactionCount

    SimulationSummary(List<SymbolSimulationSummary> transactions = []) {

        this.symbolSimulationSummaries = transactions

        this.transactionCount = transactions.size()
        this.totalInvestment = transactions.totalInvestment ? ((double)transactions.totalInvestment.sum()).round(2) : 0
        this.netProceeds = transactions.netProceeds ? ((double)transactions.netProceeds.sum()).round(2) : 0
        this.grossProceeds = transactions.grossProceeds ? ((double)transactions.grossProceeds.sum()).round(2) : 0
        this.netProceedsPercentage = totalInvestment ? ((double)(netProceeds / totalInvestment) * 100.0d).round(2) : 0
        this.grossProceedsPercentage = totalInvestment ? ((double)(grossProceeds / totalInvestment) * 100.0d).round(2) : 0
        this.totalCommissionCost = transactions.totalCommissionCost ? transactions.totalCommissionCost.sum() as double : 0
    }

}
