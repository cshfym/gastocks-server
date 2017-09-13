package com.gastocks.server.resources

import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.technical.request.MACDRequestParameters
import com.gastocks.server.models.simulation.SimulationRequest
import com.gastocks.server.models.technical.request.TechnicalQuoteRequestParameters
import com.gastocks.server.models.technical.response.TechnicalQuote
import com.gastocks.server.services.technical.TechnicalQuoteService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/technicalquote")
class TechnicalQuoteResource {

    @Autowired
    TechnicalQuoteService quoteService

    @ResponseBody
    @RequestMapping(value="/{symbol}", method=RequestMethod.POST)
    List<TechnicalQuote> getTechnicalQuote(@PathVariable("symbol") String symbol, @RequestBody TechnicalQuoteRequestParameters requestParameters) {

        def quotes

        try {
            quotes = quoteService.getTechnicalQuotesForSymbol(symbol, requestParameters)
        } catch (QuoteNotFoundException ex) {
            log.info "Quote not found for symbol [${symbol}]"
            throw ex
        }

        quotes
    }

}