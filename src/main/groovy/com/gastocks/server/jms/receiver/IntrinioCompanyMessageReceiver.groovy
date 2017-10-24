package com.gastocks.server.jms.receiver

import com.gastocks.server.jms.sender.IntrinioCompanyQueueSender
import com.gastocks.server.services.intrinio.company.CompanyService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class IntrinioCompanyMessageReceiver {

    @Autowired
    CompanyService companyService

    @JmsListener(destination = IntrinioCompanyQueueSender.QUEUE_COMPANY_DUMP_REQUEST, containerFactory = "intrinioCompanyDumpFactory")
    void receiveCompanyDumpRequest(String identifier) {

        log.debug "Received [${identifier}] from queue ${IntrinioCompanyQueueSender.QUEUE_COMPANY_DUMP_REQUEST}"

        companyService.fetchAndPersistCompanyData(identifier)
    }
}
