package com.gastocks.server.resources

import com.gastocks.server.models.BasicQuoteResponse
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.services.avtimeseriesadjusted.AVTimeSeriesAdjustedHandlerService
import com.gastocks.server.services.avtimeseriesadjusted.AVTimeSeriesAdjustedQuoteService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

/**
 * This resource handles all quote data available for a given symbol or batched symbols.
 */
@Slf4j
@Controller
@RequestMapping("/avtsa")
class AVTimeSeriesAdjustedQuoteResource {

    @Autowired
    AVTimeSeriesAdjustedQuoteService quoteService

    @Autowired
    AVTimeSeriesAdjustedHandlerService fetchAndPersistService

    /**
     * Gets a quote without persisting it.
     * @param symbol
     * @return {@BasicQuoteResponse}
     */
    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
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

    /**
     * Fetch and persist a single quote.
     * @param symbol
     * @return {@BasicQuoteResponse}
     */
    @ResponseBody
    @RequestMapping(value="/single", method=RequestMethod.POST)
    BasicQuoteResponse doSingle(@RequestParam(value="symbol", required=true) String symbol) {

        fetchAndPersistService.fetchAndQueueSymbol(symbol)

        new BasicQuoteResponse(success: true, message: "")
    }

    /**
     * Fetch and persist all quotes from a partial string parameter.
     * @param symbol
     * @return {@BasicQuoteResponse}
     */
    @ResponseBody
    @RequestMapping(value="/partial", method=RequestMethod.POST)
    BasicQuoteResponse doPartial(
            @RequestParam(value="symbol", required=true) String symbol,
            @RequestParam(value="exchange", required=true) String exchange) {

       fetchAndPersistService.fetchAndPersistQuotesPartial(symbol, exchange)

        new BasicQuoteResponse(success: true, message: "")
    }

    /**
     * Fetch and persist all quotes.
     * @return
     */
    @ResponseBody
    @RequestMapping(value="batchAll", method=RequestMethod.POST)
    BasicResponse doBatchAll() {

        fetchAndPersistService.fetchAndPersistAllQuotes()

        new BasicResponse(success: true, message: "")
    }

}