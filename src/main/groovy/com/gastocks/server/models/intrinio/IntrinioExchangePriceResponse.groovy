package com.gastocks.server.models.intrinio

class IntrinioExchangePriceResponse {

    List<IntrinioExchangePriceQuote> data

    // Metadata
    int apiCallCredits
    int currentPage
    int totalPages
    int pageSize
    int resultCount

    @Override
    String toString() {
        "IntrinioExchangePriceResponse: Page [${currentPage}] of [${totalPages}], page size [${pageSize}], " +
                "with datas [${data?.size()}], and result count [${resultCount}]"
    }
}
