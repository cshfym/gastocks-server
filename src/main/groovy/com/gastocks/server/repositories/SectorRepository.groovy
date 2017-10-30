package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableSector
import org.springframework.data.repository.CrudRepository

interface SectorRepository extends CrudRepository<PersistableSector, String> {

    PersistableSector findByDescription(String description)

}