package com.gastocks.server.jms.receiver

import com.gastocks.server.jms.models.GenericIdServiceMessage
import com.gastocks.server.jms.sender.GenericMessageQueueSender
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class GenericMessageQueueReceiver {

    @Autowired
    ApplicationContext applicationContext

    @JmsListener(destination = GenericMessageQueueSender.QUEUE_GENERIC_ID_MESSAGE, containerFactory = "genericServiceMessageFactory")
    void receiveGenericQueueRequest(GenericIdServiceMessage serviceMessage) {

        log.debug "Received generic queue request from queue ${GenericMessageQueueSender.QUEUE_GENERIC_ID_MESSAGE}"

        def serviceInvoker = applicationContext.getBean(serviceMessage.serviceClass)

        serviceInvoker.processWithIdentifier(serviceMessage.identifier)
    }
}
