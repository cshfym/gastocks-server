package com.gastocks.server.resources

import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.quote.EMAQuote
import com.gastocks.server.models.quote.Quote
import com.gastocks.server.services.EMAQuoteService
import com.gastocks.server.services.QuoteService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/emaquote")
class EMAQuoteResource {

    @Autowired
    EMAQuoteService quoteService

    @ResponseBody
    @RequestMapping(value="/{symbol}/{emashort}/{emalong}", method=RequestMethod.GET)
    List<EMAQuote> getEMAQuote(@PathVariable("symbol") String symbol, @PathVariable("emashort") int emashort, @PathVariable("emalong") int emalong) {

        def quotes = []

        try {
            quotes = quoteService.getEMAQuotesForSymbol(symbol, emashort, emalong)
        } catch (QuoteNotFoundException ex) {
            log.info "Quote not found for symbol ${symbol}"
            throw ex
        }

        quotes
    }

}