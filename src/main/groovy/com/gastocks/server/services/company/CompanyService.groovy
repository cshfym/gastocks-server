package com.gastocks.server.services.company

import com.gastocks.server.models.domain.PersistableCompanyDump
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.repositories.CompanyDumpRepository
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
class CompanyService {

    @Autowired
    CompanyDumpRepository companyDumpRepository

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    /**
     * Typically called from JMS receiver service
     * @param identifier (ticker)
     */
    @Transactional
    void loadCompanyData(String identifier) {

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)

        if (companyDumpRepository.findBySymbol(symbol)) {
            log.info("CompanyDump record already exists for [${identifier}], bypassing.")
            return
        }

        def companyJson = getCompanyData(identifier)

        PersistableCompanyDump companyDump = new PersistableCompanyDump(
            symbol: symbol,
            jsonDump: companyJson
        )

        companyDumpRepository.save(companyDump)
    }

    // TODO Move below into a common Intrinio service.

    @Value('${intrinio.base.url}')
    String INTRINIO_BASE_URL

    @Value('${intrinio.api.key}')
    String INTRINIO_API_KEY

    @Value('${intrinio.api.password}')
    String INTRINIO_API_PASSWORD

    String INTRINIO_COMPANY_PATH = "/companies"

    Object getCompanyData(String identifier) {

        def companyData = null

        def startStopwatch = System.currentTimeMillis()

        HttpURLConnection conn = null

        try {
            URL url = new URL(INTRINIO_BASE_URL + INTRINIO_COMPANY_PATH + "?ticker=${identifier}")
            conn = (HttpURLConnection) url.openConnection()
            conn.setRequestMethod("GET")
            conn.setRequestProperty("Accept", "application/json")

            String userCredentials = INTRINIO_API_KEY + ":" + INTRINIO_API_PASSWORD
            String base64EncodedCredentials = userCredentials.bytes.encodeBase64().toString()
            String basicAuth = "Basic " + base64EncodedCredentials
            conn.setRequestProperty("Authorization", basicAuth)

            conn.connect()

            def responseCode = conn.responseCode

            switch (responseCode) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.inputStream))
                    StringBuilder sb = new StringBuilder()
                    String line
                    while ((line = br.readLine()) != null) { sb.append(line) }
                    br.close()
                    companyData = sb.toString()
                    break
                case 401:
                    log.warn("401 Unauthorized from [${url.text}] for identifier [${identifier}]")
                    break
                case 404:
                    log.warn("No company data found at [${url.text}] for identifier [${identifier}]")
                    break
                default:
                    throw new RuntimeException("Failed : HTTP error code [${responseCode}]")
            }
            log.trace "Slurped data at [${url.path}] for symbol [${identifier}]: [${companyData}]"
        } catch (Exception ex) {
            log.error("Exception caught processing [${identifier}]: [${ex.message}]")
            return null
        } finally {
            conn?.disconnect()
        }

        log.info "Company dump for symbol [${identifier}] retrieved in [${System.currentTimeMillis() - startStopwatch} ms]"

        companyData
    }

}
