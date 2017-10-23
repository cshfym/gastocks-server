package com.gastocks.server.jms.receiver

import com.gastocks.server.jms.sender.CompanyQueueSender
import com.gastocks.server.services.company.CompanyService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class CompanyMessageReceiver {

    @Autowired
    CompanyService companyService

    @JmsListener(destination = CompanyQueueSender.QUEUE_COMPANY_DUMP_REQUEST, containerFactory = "companyDumpFactory")
    void receiveCompanyDumpRequest(String identifier) {

        log.debug "Received [${identifier}] from queue ${CompanyQueueSender.QUEUE_COMPANY_DUMP_REQUEST}"

        companyService.loadCompanyData(identifier)
    }
}
