package com.gastocks.server.models.simulation

/**
 * Represents a single transaction for a single symbol in the simulation
 */
class SimulationTransaction {

    // Base attributes
    double commission
    Date purchaseDate
    double purchasePrice
    Date sellDate
    double sellPrice
    String symbol
    int shares

    // Calculated properties

    /**
     * Gross proceeds and percentage are the earning/loss * shares purchased
     */
    double grossProceeds
    double grossProceedsPercentage

    /**
     * Net proceeds and percentage are gross proceeds minus commission
     */
    double netProceeds
    double netProceedsPercentage

    /**
     * Total investment is the total proceeds required to purchase the shares
     */
    double totalInvestment

    /**
     * Investment periods - how long the investment was held before closing (selling)
     */
    int investmentPeriodDays


    SimulationTransaction(double commission, Date purchaseDate, double purchasePrice, Date sellDate, double sellPrice, String symbol, int shares) {

        this.commission = commission
        this.purchaseDate = purchaseDate
        this.purchasePrice = purchasePrice
        this.sellDate = sellDate
        this.sellPrice = sellPrice
        this.symbol = symbol
        this.shares = shares

        // Calculate derived attributes
        this.grossProceeds = (((sellPrice - purchasePrice) * shares) - commission).round(2)
        this.totalInvestment = (purchasePrice * shares).round(2)
        this.netProceeds = (grossProceeds - commission).round(2)
        this.grossProceedsPercentage = ((double)(grossProceeds / totalInvestment) * 100.0d).round(2)
        this.netProceedsPercentage = ((double)(netProceeds / totalInvestment) * 100.0d).round(2)

        this.investmentPeriodDays = Math.abs(sellDate.time - purchaseDate.time) / (24 * 60 * 60 * 1000)
    }
}
