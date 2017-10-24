package com.gastocks.server.models.intrinio

class IntrinioExchangeRequest {

    String exchange
    String date
    int startPage

    @Override
    String toString() {
        "IntrinioExchangeRequest: exchange [${exchange}], date [${date}], startPage [${startPage}]"
    }
}
