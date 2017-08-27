package com.gastocks.server.resources

import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.simulation.MACDRequestParameters
import com.gastocks.server.models.simulation.SimulationRequest
import com.gastocks.server.models.technical.TechnicalQuote
import com.gastocks.server.services.TechnicalQuoteService
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
    @RequestMapping(value="/{symbol}/{emashort}/{emalong}", method=RequestMethod.GET)
    List<TechnicalQuote> getTechnicalQuote(@PathVariable("symbol") String symbol, @PathVariable("emashort") int emashort, @PathVariable("emalong") int emalong) {

        def quotes = []

        try {
            quotes = quoteService.getTechnicalQuotesForSymbol(symbol,
                new SimulationRequest(macdParameters: new MACDRequestParameters(macdShortPeriod: emashort, macdLongPeriod: emalong)))
        } catch (QuoteNotFoundException ex) {
            log.info "Quote not found for symbol [${symbol}]"
            throw ex
        }

        quotes
    }

}