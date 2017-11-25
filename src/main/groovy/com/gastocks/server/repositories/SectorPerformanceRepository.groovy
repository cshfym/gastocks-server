package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.models.domain.PersistableSectorPerformance
import org.springframework.data.repository.CrudRepository

interface SectorPerformanceRepository extends CrudRepository<PersistableSectorPerformance, String> {

    PersistableSectorPerformance findByQuoteDateAndSector(Date quoteDate, PersistableSector sector)

    List<PersistableSectorPerformance> findAllBySector(PersistableSector sector)
}