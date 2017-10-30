package com.gastocks.server.services.intrinio.company

import com.gastocks.server.converters.company.CompanyConverter
import com.gastocks.server.jms.models.GenericIdServiceMessage
import com.gastocks.server.jms.models.IGenericServiceInvoker
import com.gastocks.server.jms.sender.GenericMessageQueueSender
import com.gastocks.server.models.domain.PersistableCompany
import com.gastocks.server.models.domain.PersistableCompanyDump
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.repositories.CompanyDumpRepository
import com.gastocks.server.services.HTTPConnectionService
import com.gastocks.server.services.domain.SymbolPersistenceService
import com.gastocks.server.services.intrinio.IntrinioBaseService
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

import javax.transaction.Transactional

@Slf4j
@Service
class CompanyService extends IntrinioBaseService implements IGenericServiceInvoker {

    @Autowired
    CompanyDumpRepository companyDumpRepository

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    HTTPConnectionService connectionService

    @Autowired
    GenericMessageQueueSender genericMessageQueueSender

    @Autowired
    CompanyConverter companyConverter

    /**
     * Typically called from JMS receiver service
     * @param identifier (ticker)
     */
    @Transactional
    void fetchAndPersistCompanyData(String identifier) {

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)

        if (companyDumpRepository.findBySymbol(symbol)) {
            log.info("CompanyDump record already exists for [${identifier}], bypassing.")
            return
        }

        String uri = INTRINIO_BASE_URL + INTRINIO_COMPANY_PATH + "?ticker=${identifier}"

        String userCredentials = INTRINIO_API_KEY + ":" + INTRINIO_API_PASSWORD
        String base64EncodedCredentials = userCredentials.bytes.encodeBase64().toString()

        String companyResponse = connectionService.getData(uri, RequestMethod.GET, base64EncodedCredentials)

        if (companyResponse) {
            PersistableCompanyDump companyDump = new PersistableCompanyDump(
                    symbol: symbol,
                    jsonDump: companyResponse
            )

            companyDumpRepository.save(companyDump)
        }
    }

    void fetchAndQueueAllCompanyDumpRecords() {

        List<PersistableCompanyDump> companyDumpList = companyDumpRepository.findAll()

        companyDumpList.eachWithIndex { companyDump, ix ->

            genericMessageQueueSender.queueRequest(
                new GenericIdServiceMessage(identifier: companyDump.id, serviceClass: this.class)
            )
        }

    }

    /**
     * Called from the JMS queue receiver
     */
    void processWithIdentifier(String identifier) {

        PersistableCompanyDump companyDump = companyDumpRepository.findOne(identifier)

        try {
            PersistableCompany company = companyConverter.fromJson(companyDump.jsonDump)
            log.info("${company}")
        } catch (Exception ex) {
            log.error("Error: ", ex)
        }
    }

}
