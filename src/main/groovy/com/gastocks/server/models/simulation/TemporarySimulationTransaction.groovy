package com.gastocks.server.models.simulation


class TemporarySimulationTransaction {

    TemporarySimulationTransaction() {
        shares = 0
        purchasePrice = 0.0d
        sellPrice = 0.0d
    }

    String symbol

    Date purchaseDate
    double purchasePrice

    Date sellDate
    double sellPrice

    int shares

    double commission

    boolean isStarted() {
        purchaseDate && !sellDate
    }

    boolean isFinalized() {
        purchaseDate && sellDate
    }

    double getGrossProceeds() {
        (netProceeds - commission).round(2)
    }

    double getNetProceeds() {

        if (!finalized) {
            return 0.0d
        }

        (((sellPrice - purchasePrice) * shares) - commission).round(2)
    }

    double getTotalInvestment() {
        (purchasePrice * shares).round(2)
    }

    double getGrossPercentage() {
        double grossPercentage = (double)(grossProceeds / totalInvestment)
        (grossPercentage * 100.0d).round(2)
    }

    double getNetPercentage() {
        double netPercentage = (double)(netProceeds / totalInvestment)
        (netPercentage * 100.0d).round(2)
    }

    @Override
    String toString() {
        "Transaction purchase of [${symbol}] on date [${purchaseDate}] for [${purchasePrice}], sell date [${sellDate}] for [${sellPrice}], " +
            "gross proceeds of [${grossProceeds}] (${grossPercentage}%), net proceeds of [${netProceeds}] (${netPercentage})%."
    }
}
