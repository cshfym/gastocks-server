package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableCompanyDump
import com.gastocks.server.repositories.CompanyDumpRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class CompanyDumpPersistenceService {

    @Autowired
    CompanyDumpRepository companyDumpRepository

    void persistCompanyDump(PersistableCompanyDump companyDump) {
        companyDumpRepository.save(companyDump)
    }

}
