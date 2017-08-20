package com.gastocks.server.models.simulation

import java.beans.Transient

class SymbolSimulation {

    String symbol

    List<SimulationTransaction> stockTransactions

    @Transient
    double getNetProceeds() {
        double totalBalance = 0.0d
        stockTransactions.each { transaction -> totalBalance += transaction.netProceeds }
        totalBalance.round(2)
    }

    @Transient
    double getGrossProceeds() {
        double totalBalance = 0.0d
        stockTransactions.each { transaction -> totalBalance += transaction.grossProceeds }
        totalBalance.round(2)
    }

    @Transient
    double getTotalCommissionCost() {
        double totalCommission = 0.0d
        stockTransactions.each { transaction -> totalCommission += transaction.commission }
        totalCommission.round(2)
    }

    @Transient
    List<String> getTransactionDetails() {
        List<String> purchaseDates = []
        stockTransactions.each { transaction -> purchaseDates << transaction.toString() }
        purchaseDates
    }

    @Transient
    double getInvestmentTotal() {
        double transactionTotal = 0.0d
        stockTransactions.each { transaction -> transactionTotal += transaction.totalInvestment }
        transactionTotal.round(2)
    }

    @Transient
    double getInvestmentTotalPercentage() {
        double totalPercentage = (double)(netProceeds / investmentTotal)
        (totalPercentage * 100.0d).round(2)
    }

    @Transient
    SimulationSummary getSummary() {
        new SimulationSummary(
            symbol: symbol,
            totalInvestment: investmentTotal,
            totalEarningsPercentage: investmentTotalPercentage,
            totalCommissionCost: totalCommissionCost,
            transactionDetails: transactionDetails,
            netProceeds: netProceeds,
            grossProceeds: grossProceeds,
            transactionCount: stockTransactions.size()
        )
    }
}
