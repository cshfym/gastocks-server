package com.gastocks.server.services.intrinio.exchangeprices

import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.intrinio.IntrinioExchangeRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExchangePriceApiService {

    @Autowired
    ExchangePriceService exchangePriceService

    BasicResponse backfill(IntrinioExchangeRequest request) {

        exchangePriceService.fetchAndPersistExchangePrices(request)
        new BasicResponse(success: true, message: "Completed Intrinio exchange price request [${request}]")
    }

}
