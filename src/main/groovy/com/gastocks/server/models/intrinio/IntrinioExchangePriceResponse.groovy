package com.gastocks.server.models.intrinio

class IntrinioExchangePriceResponse {

    List<IntrinioExchangePriceQuote> data

    // Metadata
    int apiCallCredits
    int currentPage
    int totalPages
    int pageSize
    int resultCount
}
