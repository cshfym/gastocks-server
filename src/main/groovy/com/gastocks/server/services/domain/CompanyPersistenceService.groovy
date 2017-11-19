package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableCompany
import com.gastocks.server.models.domain.PersistableCompanyDump
import com.gastocks.server.repositories.CompanyDumpRepository
import com.gastocks.server.repositories.CompanyRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class CompanyPersistenceService {

    @Autowired
    CompanyDumpRepository companyDumpRepository

    @Autowired
    CompanyRepository companyRepository

    @Transactional
    PersistableCompanyDump persistCompanyDump(PersistableCompanyDump companyDump) {
        companyDumpRepository.save(companyDump)
    }

    @Transactional
    void deleteCompanyDump(PersistableCompanyDump companyDump) {
        companyDumpRepository.delete(companyDump)
    }

    @Transactional
    PersistableCompany persistCompany(PersistableCompany company) {
        companyRepository.save(company)
    }

}
