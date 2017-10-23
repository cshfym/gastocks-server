package com.gastocks.server.services.company

import com.gastocks.server.jms.sender.CompanyQueueSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.symbol.Symbol
import com.gastocks.server.services.SymbolService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompanyApiService {

    @Autowired
    SymbolService symbolService

    @Autowired
    CompanyQueueSender companyQueueSender


    BasicResponse backfill() {

        List<Symbol> allSymbols = symbolService.findAllSymbols()

        allSymbols.eachWithIndex { symbol, index ->
            companyQueueSender.queueRequest(symbol.identifier)
        }

        new BasicResponse(success: true, message: "Queued [${allSymbols.size()}] symbols for company dump")
    }

}
