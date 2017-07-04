package com.gastocks.server.models

class Quote {

    String symbol
    double open
    double high
    double close

    Quote(String symbol, double open, double high, double close) {
        this.symbol = symbol
        this.open = open
        this.high = high
        this.close = close
    }
}
