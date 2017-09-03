package com.gastocks.server.resources

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
    BasicResponse backfill() {
        symbolService.backfillAllSymbols()
    }
}