package com.gastocks.server.models.exchangemarket


class ExchangeMarketConstants {

    static final String NEW_YORK_STOCK_EXCHANGE = "NYSE"
    static final String NASDAQ_STOCK_EXCHANGE = "NASDAQ"

    final static List<String> NASDAQ_ALTERNATES = ["NASDAQGS","NASDAQCM"]
    final static List<String> NEW_YORK_STOCK_EXCHANGE_ALTERNATES = ["NYSE MKT","NYSE ARCA"]

    static String resolveMarketName(String market) {

        if (market.toUpperCase().equals(NEW_YORK_STOCK_EXCHANGE) || isNYSEAlternate(market)) {
            return NEW_YORK_STOCK_EXCHANGE
        }

        if (market.toUpperCase().equals(NASDAQ_STOCK_EXCHANGE) || isNASDAQAlternate(market)) {
            return NASDAQ_STOCK_EXCHANGE
        }

        ""
    }

    private static boolean isNASDAQAlternate(String value) {
        NASDAQ_ALTERNATES.contains(value.toUpperCase())
    }

    private static boolean isNYSEAlternate(String value) {
        NEW_YORK_STOCK_EXCHANGE_ALTERNATES.contains(value.toUpperCase())
    }
}
