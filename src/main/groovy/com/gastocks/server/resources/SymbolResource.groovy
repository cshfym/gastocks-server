package com.gastocks.server.resources

import com.gastocks.server.jms.sender.SymbolExtendedQueueSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.domain.ViewSymbolExtended
import com.gastocks.server.models.symbol.Symbol
import com.gastocks.server.services.SymbolService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/symbols")
class SymbolResource {

    @Autowired
    SymbolService symbolService

    @Autowired
    SymbolExtendedQueueSender symbolExtendedQueueSender

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    List<Symbol> getSymbols() {
        symbolService.findAllSymbols()
    }

    @ResponseBody
    @RequestMapping(value="/vse", method=RequestMethod.GET)
    List<ViewSymbolExtended> getViewSymbolExtended(
        @RequestParam(value="maxQuotePrice", required=false) Double maxQuotePrice,
        @RequestParam(value="minQuotePrice", required=false) Double minQuotePrice) {
        symbolService.findAllViewSymbolExtendedWithParameters(maxQuotePrice, minQuotePrice)
    }

    /**
     * Convenience API to trigger backfill of all symbol_extended records.
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/backfill", method=RequestMethod.POST)
    BasicResponse backfillAll() {
        symbolService.backfillAllSymbols()
    }

    /**
     * Convenience API to trigger backfill of a single symbol_extended record.
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/backfill/{identifier}", method=RequestMethod.POST)
    BasicResponse backfillSingle(@PathVariable("identifier") String identifier) {
        symbolExtendedQueueSender.queueRequest(identifier)
    }

    /**
     * Convenience API to trigger backfill of previous day close, price change, price change %
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/pricechange", method=RequestMethod.POST)
    BasicResponse priceChangeAll() {
        symbolService.backfillAllSymbolsPriceChangeData()
    }

    /**
     * Convenience API to trigger backfill of previous day close, price change, price change % for a single symbol
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/pricechange/{identifier}", method=RequestMethod.POST)
    BasicResponse priceChangeWithIdentifier(@PathVariable("identifier") String identifier) {
        symbolService.backfillSymbolPriceChangeData(identifier)
    }
}