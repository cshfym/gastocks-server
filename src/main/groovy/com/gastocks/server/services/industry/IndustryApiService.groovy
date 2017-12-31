package com.gastocks.server.services.industry

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.models.domain.PersistableIndustry
import com.gastocks.server.services.domain.IndustryPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Slf4j
@Service
class IndustryApiService {

    @Autowired
    IndustryPersistenceService industryPersistenceService

    @Cacheable(value = CacheConfiguration.ALL_INDUSTRIES)
    List<PersistableIndustry> findAllIndustries() {
        industryPersistenceService.findAllIndustries()?.sort { i1, i2 -> i1.category <=> i2.category }
    }
}
