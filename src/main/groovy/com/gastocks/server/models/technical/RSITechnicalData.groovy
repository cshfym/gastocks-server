package com.gastocks.server.models.technical

class RSITechnicalData {

    /**
     * Interval is the number of data points back for calculating the RSI, with a default of 14.
     */
    int interval

    double priceGain
    double priceLoss

}
