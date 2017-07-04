package com.gastocks.server.resources

import com.gastocks.server.models.Quote
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

    @RequestMapping(method=RequestMethod.GET)
    @ResponseBody Quote getQuote(@RequestParam(value="symbol", required=true) String symbol) {

        quoteService.getQuote(symbol)

        new Quote("XYZ", 1.0d, 2.0d, 3.0d)
    }
}