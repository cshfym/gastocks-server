package com.gastocks.server.services.domain

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.models.domain.ViewSymbolExtended
import com.gastocks.server.repositories.ViewSymbolExtendedRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class ViewSymbolExtendedCacheService {

    @Autowired
    ViewSymbolExtendedRepository viewSymbolExtendedRepository

    @Cacheable(value = CacheConfiguration.VIEW_SYMBOL_EXTENDED)
    List<ViewSymbolExtended> findAllViewSymbolExtendedFromCache() {

        def startStopwatch = System.currentTimeMillis()

        def response = viewSymbolExtendedRepository.findAll()
        log.info("Found ALL [${response.size()}] ViewSymbolExtended in [${System.currentTimeMillis() - startStopwatch} ms] ")

        response
    }


}
