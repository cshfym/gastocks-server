package com.gastocks.server.models.simulation

/**
 *
 */
class StockTransaction {

    StockTransaction() {
        shares = 0
        purchasePrice = 0.0d
        sellPrice = 0.0d
    }

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

    double getGross() {
        (net - commission).round(2)
    }

    double getNet() {

        if (!finalized) {
            return 0.0d
        }

        (((sellPrice - purchasePrice) * shares) - commission).round(2)
    }

    @Override
    String toString() {
        "Transaction purchase date [${purchaseDate}] for [${purchasePrice}], sell date [${sellDate}] for [${sellPrice}], " +
            "gross proceeds of [${gross}], net proceeds of [${net}]."
    }
}
