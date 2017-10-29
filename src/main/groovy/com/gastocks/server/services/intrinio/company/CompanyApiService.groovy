package com.gastocks.server.services.intrinio.company

import com.gastocks.server.jms.sender.IntrinioCompanyQueueSender
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
    IntrinioCompanyQueueSender companyQueueSender

    @Autowired
    CompanyService companyService

    BasicResponse backfill() {

        List<Symbol> allSymbols = symbolService.findAllSymbols()

        allSymbols.eachWithIndex { symbol, index ->
            companyQueueSender.queueRequest(symbol.identifier)
        }

        new BasicResponse(success: true, message: "Queued [${allSymbols.size()}] symbols for company dump")
    }

    BasicResponse convertall() {
        companyService.fetchAndConvertCompanyDumpData()
    }
}
