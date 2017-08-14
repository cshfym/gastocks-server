package com.gastocks.server.resources

import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.simulation.SimulationRequest
import com.gastocks.server.services.simulation.SimulationHandlerService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/simulation")
class SimulationResource {

    @Autowired
    SimulationHandlerService simulationHandlerService

    @ResponseBody
    @RequestMapping(method=RequestMethod.POST)
    BasicResponse doBasicSimulation(@RequestBody SimulationRequest request) {
        simulationHandlerService.queueSimulationRequest(request)
    }

    /*
    @ResponseBody
    @RequestMapping(value="/{symbol}", method=RequestMethod.GET)
    SimulationSummary doBasicSimulation(@PathVariable("symbol") String symbol,
                                        @RequestParam("emaShort") int emaShort, @RequestParam("emaLong") int emaLong,
                                        @RequestParam("aboveCenter") boolean aboveCenter) {
        simulationService.doSimulationForSymbol(symbol, emaShort, emaLong, aboveCenter)
    }

    @ResponseBody
    @RequestMapping(value="/csvsummary/{symbol}", method=RequestMethod.GET)
    List<String> doBasicSimulationWithCSV(@PathVariable("symbol") String symbol,
                                        @RequestParam("emaShort") int emaShort, @RequestParam("emaLong") int emaLong,
                                        @RequestParam("aboveCenter") boolean aboveCenter) {
        simulationService.doSimulationForSymbolWithCSV(symbol, emaShort, emaLong, aboveCenter)
    }

    @ResponseBody
    @RequestMapping(value="/csvsummary", method=RequestMethod.GET)
    List<String> doBasicSimulationWithCSVAllSymbols(@RequestParam("emaShort") int emaShort, @RequestParam("emaLong") int emaLong,
                                          @RequestParam("aboveCenter") boolean aboveCenter, @RequestParam("count") int count) {

        simulationService.doSimulationForAllSymbolsWithCSV(emaShort, emaLong, aboveCenter, count)
    }
    */

}