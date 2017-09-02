package com.gastocks.server.resources

import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.symbol.EnhancedSymbol
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

    /**
     * Finds all enhanced symbols which fit the constraint parameters.
     * @param high52Week
     * @param low52Week
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/enhanced", method=RequestMethod.GET)
    List<EnhancedSymbol> getEnhancedSymbols(@RequestParam("high52Week") double high52Week, @RequestParam("low52Week") double low52Week) {
        symbolService.findAllEnhancedSymbols(high52Week, low52Week)
    }

    @ResponseBody
    @RequestMapping(value="/backfill", method=RequestMethod.POST)
    BasicResponse backfill() {
        symbolService.backfillAllSymbols()
    }
}