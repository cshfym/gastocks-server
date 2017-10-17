package com.gastocks.server.resources

import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.quote.Quote
import com.gastocks.server.services.QuoteService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/quote")
class QuoteResource {

    @Autowired
    QuoteService quoteService

    @ResponseBody
    @RequestMapping(value="/{symbol}", method=RequestMethod.GET)
    List<Quote> getQuote(@PathVariable("symbol") String symbol) {

        def quotes = []

        try {
            quotes = quoteService.getQuotesForSymbol(symbol)
        } catch (QuoteNotFoundException ex) {
            log.info "Quote not found for symbol ${symbol}"
            throw ex
        }

        quotes
    }

    @ResponseBody
    @RequestMapping(value="/audit", method=RequestMethod.POST)
    BasicResponse doQuoteAudit() {
        quoteService.queueAllSymbolsForQuoteAudit()
    }

    @ResponseBody
    @RequestMapping(value="/audit/reload", method=RequestMethod.POST)
    BasicResponse doQuoteAuditReload() {
        quoteService.doQueueSymbolsForAuditReload()
    }

}