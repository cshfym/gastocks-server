package com.gastocks.server.resources

import com.gastocks.server.models.avglobalquote.AVGlobalQuoteResponse
import com.gastocks.server.services.avglobalquote.AVGlobalQuoteService
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/avglobalquote")
class AVGlobalQuoteResource {

    @Autowired
    AVGlobalQuoteService quoteService

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    AVGlobalQuoteResponse getQuote(@RequestParam(value="symbol", required=true) String symbol) {

        def quote = quoteService.getQuote(symbol)

        if (quote) {
            new AVGlobalQuoteResponse(
                success: true,
                response: "",
                quote: quote)
        } else {
            new AVGlobalQuoteResponse(success: false, response: "Not found")
        }

    }
}