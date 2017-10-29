package com.gastocks.server.services.intrinio.company

import com.gastocks.server.models.domain.PersistableCompanyDump
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.repositories.CompanyDumpRepository
import com.gastocks.server.services.HTTPConnectionService
import com.gastocks.server.services.domain.SymbolPersistenceService
import com.gastocks.server.services.intrinio.IntrinioBaseService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

import javax.transaction.Transactional

@Slf4j
@Service
class CompanyService extends IntrinioBaseService {

    @Autowired
    CompanyDumpRepository companyDumpRepository

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    HTTPConnectionService connectionService


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

    void fetchAndConvertCompanyDumpData() {

        List<PersistableCompanyDump> companyDumpList = companyDumpRepository.findAll()

        companyDumpList.each { companyDump ->

        }


    }

}
