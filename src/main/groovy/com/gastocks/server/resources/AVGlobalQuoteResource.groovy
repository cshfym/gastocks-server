package com.gastocks.server.resources

import com.gastocks.server.models.BasicQuoteResponse
import com.gastocks.server.services.avglobalquote.AVGlobalQuoteHandlerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

/**
 * This resource handles the most recent/current quote for a given symbol or all batched symbols.
 */
@Controller
@RequestMapping("/avgq")
class AVGlobalQuoteResource {

    @Autowired
    AVGlobalQuoteHandlerService quoteHandlerService

    @ResponseBody
    @RequestMapping(value="/batchAll", method=RequestMethod.GET)
    BasicQuoteResponse batchAll() {

        quoteHandlerService.fetchAndQueueAllQuotesForAllSymbols()

        new BasicQuoteResponse(success: true, message: "", quote: null)
    }

    @ResponseBody
    @RequestMapping(value="/single", method=RequestMethod.GET)
    BasicQuoteResponse doSingle(@RequestParam(value="symbol", required=true) String symbol) {

        quoteHandlerService.fetchAndQueueQuoteForSymbol(symbol)

        new BasicQuoteResponse(success: true, message: "", quote: null)
    }
}