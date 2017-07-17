package com.gastocks.server.resources

import com.gastocks.server.models.BasicQuoteResponse
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Slf4j
@Controller
@RequestMapping("/scheduler")
class ScheduleResource {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @ResponseBody
    @RequestMapping(value="/missing", method=RequestMethod.GET)
    BasicQuoteResponse loadMissingQuotes() {

        List<PersistableSymbol> symbolsWithMissingQuotes = symbolPersistenceService.findSymbolsWithMissingQuotes()

        symbolsWithMissingQuotes.each { symbol ->
            log.info("Missing quote for symbol: [${symbol.identifier}]")
        }

        new BasicQuoteResponse(success: true, message: "")
    }

}