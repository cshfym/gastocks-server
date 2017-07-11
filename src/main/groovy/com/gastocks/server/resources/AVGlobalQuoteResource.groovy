package com.gastocks.server.resources

import com.gastocks.server.models.BasicQuoteResponse
import com.gastocks.server.services.avglobalquote.AVGlobalQuoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@Controller
@RequestMapping("/avtsaq")
class AVGlobalQuoteResource {

    @Autowired
    AVGlobalQuoteService quoteService

    @ResponseBody
    @RequestMapping(value="/quote", method=RequestMethod.GET)
    BasicQuoteResponse getQuote(@RequestParam(value="symbol", required=true) String symbol) {

        def quote = quoteService.getQuote(symbol)

        if (quote) {
            new BasicQuoteResponse(
                success: true,
                message: "",
                quote: quote)
        } else {
            new BasicQuoteResponse(success: false, message: "Not found")
        }

    }
}