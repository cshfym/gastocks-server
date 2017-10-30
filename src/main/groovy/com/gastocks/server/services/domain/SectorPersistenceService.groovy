package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.repositories.SectorRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class SectorPersistenceService {

    @Autowired
    SectorRepository sectorRepository

    PersistableSector findByDescription(String description) {
        sectorRepository.findByDescription(description)
    }

}
