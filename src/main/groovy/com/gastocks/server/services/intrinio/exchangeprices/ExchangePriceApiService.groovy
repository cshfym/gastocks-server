package com.gastocks.server.services.intrinio.exchangeprices

import com.gastocks.server.jms.sender.IntrinioExchangeQueueSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.intrinio.IntrinioExchangeRequest
import com.gastocks.server.services.SymbolService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExchangePriceApiService {

    @Autowired
    SymbolService symbolService

    @Autowired
    IntrinioExchangeQueueSender exchangeQueueSender


    BasicResponse backfill(IntrinioExchangeRequest request) {

        exchangeQueueSender.queueRequest(request)

        new BasicResponse(success: true, message: "Queued Intrinio exchange price request [${request}]")
    }

}
