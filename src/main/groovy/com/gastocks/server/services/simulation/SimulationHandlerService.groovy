package com.gastocks.server.services.simulation

import com.gastocks.server.jms.sender.SimulationQueueSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.simulation.SimulationRequest
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
@CompileStatic
@Transactional
class SimulationHandlerService {

    @Autowired
    SimulationQueueSender queueSender

    BasicResponse queueSimulationRequest(SimulationRequest request) {
        queueSender.queueRequest(request)
        new BasicResponse(success: true)
    }
}
