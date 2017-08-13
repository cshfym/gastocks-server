package com.gastocks.server.models.simulation

import java.beans.Transient

class BasicSimulation {

    List<StockTransaction> stockTransactions

    @Transient
    double getNetProceeds() {

        double totalBalance = 0.0d

        stockTransactions.each { transaction ->
            totalBalance += transaction.net
        }

        totalBalance.round(2)
    }

    @Transient
    double getGrossProceeds() {

        double totalBalance = 0.0d

        stockTransactions.each { transaction ->
            totalBalance += transaction.gross
        }

        totalBalance.round(2)
    }
}
