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

}