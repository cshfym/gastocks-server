package com.gastocks.server.resources

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

}