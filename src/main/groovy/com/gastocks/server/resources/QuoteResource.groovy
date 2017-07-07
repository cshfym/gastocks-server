package com.gastocks.server.resources

import com.gastocks.server.models.Quote
import com.gastocks.server.models.QuoteResponse
import com.gastocks.server.services.QuoteService
import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/quote")
class QuoteResource {

    @Autowired
    QuoteService quoteService

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    QuoteResponse getQuote(@RequestParam(value="symbol", required=true) String symbol) {

        def quote = quoteService.getQuote(symbol)

        if (quote) {
            new QuoteResponse(
                success: true,
                response: "",
                quote: quote)
        } else {
            new QuoteResponse(success: false, response: "Not found")
        }

    }
}