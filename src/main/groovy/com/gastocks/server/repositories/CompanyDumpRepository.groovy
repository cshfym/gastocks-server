package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableCompanyDump
import com.gastocks.server.models.domain.PersistableSymbol
import org.springframework.data.repository.CrudRepository

interface CompanyDumpRepository extends CrudRepository<PersistableCompanyDump, String> {

    PersistableCompanyDump findBySymbol(PersistableSymbol symbol)
}