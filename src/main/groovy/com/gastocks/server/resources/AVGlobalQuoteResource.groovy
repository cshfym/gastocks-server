package com.gastocks.server.resources

import com.gastocks.server.models.BasicQuoteResponse
import com.gastocks.server.services.avglobalquote.AVGlobalQuoteHandlerService
import com.gastocks.server.services.avglobalquote.AVGlobalQuoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@Controller
@RequestMapping("/avgq")
class AVGlobalQuoteResource {

    @Autowired
    AVGlobalQuoteHandlerService quoteHandlerService

    @ResponseBody
    @RequestMapping(value="/batchAll", method=RequestMethod.GET)
    BasicQuoteResponse getQuote() {

        def quote = quoteHandlerService.fetchAndQueueAllQuotesForAllSymbols()

        new BasicQuoteResponse(
            success: true,
            message: "",
            quote: quote)
    }
}