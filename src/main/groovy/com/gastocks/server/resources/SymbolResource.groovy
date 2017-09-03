package com.gastocks.server.resources

import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.symbol.EnhancedSymbol
import com.gastocks.server.models.symbol.Symbol
import com.gastocks.server.services.SymbolService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver

import javax.websocket.server.PathParam

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
     * Finds all enhanced symbols with parameters.
     * @param high52Week
     * @param low52Week
     * @return List<EnhancedSymbol>
     */
    @ResponseBody
    @RequestMapping(value="/enhanced", method=RequestMethod.GET)
    List<EnhancedSymbol> getEnhancedSymbols(@RequestParam(value="count", required=true) int count,
            @RequestParam(value="maxQuotePrice", required=false) Double maxQuotePrice,
            @RequestParam(value="minQuotePrice", required=false) Double minQuotePrice) {
        symbolService.findAllEnhancedSymbols(count, maxQuotePrice, minQuotePrice)
    }

    /**
     * Find single enhanced symbol with identifier path parameter.
     * @param identifier
     * @return EnhancedSymbol
     */
    @ResponseBody
    @RequestMapping(value="/enhanced/{symbol_id}", method=RequestMethod.GET)
    EnhancedSymbol getEnhancedSymbolByIdentifier(@PathVariable("symbol_id") String identifier) {
        symbolService.findEnhancedSymbolByIdentifier(identifier)
    }

    @ResponseBody
    @RequestMapping(value="/backfill", method=RequestMethod.POST)
    BasicResponse backfill() {
        symbolService.backfillAllSymbols()
    }
}