package com.gastocks.server.services.sector

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.services.domain.SectorPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Slf4j
@Service
class SectorApiService {

    @Autowired
    SectorPersistenceService sectorPersistenceService

    @Cacheable(value = CacheConfiguration.ALL_SECTORS)
    List<PersistableSector> findAllSectors() {
        sectorPersistenceService.findAll()?.sort { s1, s2 -> s1.description <=> s2.description }
    }
}
