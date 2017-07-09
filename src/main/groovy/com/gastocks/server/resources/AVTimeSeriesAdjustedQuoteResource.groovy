package com.gastocks.server.resources

import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedQuoteResponse
import com.gastocks.server.services.AVTimeSeriesAdjustedQuoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/avtimeseriesadjustedquote")
class AVTimeSeriesAdjustedQuoteResource {

    @Autowired
    AVTimeSeriesAdjustedQuoteService quoteService

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    AVTimeSeriesAdjustedQuoteResponse getQuote(@RequestParam(value="symbol", required=true) String symbol) {

        def quote = quoteService.getQuote(symbol)

        if (quote) {
            new AVTimeSeriesAdjustedQuoteResponse(
                success: true,
                response: "",
                quote: quote)
        } else {
            new AVTimeSeriesAdjustedQuoteResponse(success: false, response: "Not found")
        }

    }
}