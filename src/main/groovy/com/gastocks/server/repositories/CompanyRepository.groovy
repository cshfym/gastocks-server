package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableCompany
import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.models.domain.PersistableSymbol
import org.springframework.data.repository.CrudRepository

interface CompanyRepository extends CrudRepository<PersistableCompany, String> {

    List<PersistableCompany> findAllBySector(PersistableSector sector)

    PersistableCompany findBySymbol(PersistableSymbol symbol)

}