package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.ViewSymbolExtended
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class ViewSymbolExtendedPersistenceService {

    @Autowired
    ViewSymbolExtendedCacheService viewSymbolExtendedCacheService

    List<ViewSymbolExtended> findAllWithParameters(Double maxQuotePrice = null, Double minQuotePrice = null) {

        def startStopwatch = System.currentTimeMillis()

        def filteredEntries = []
        def allEntries = viewSymbolExtendedCacheService.findAllViewSymbolExtendedFromCache()

        allEntries.each { viewSymbolExtended ->
            if (maxQuotePrice && minQuotePrice) {
                if ((viewSymbolExtended.maxPrice <= maxQuotePrice) && (viewSymbolExtended.minPrice >= minQuotePrice)) {
                    filteredEntries << viewSymbolExtended
                }
            } else {
                filteredEntries << viewSymbolExtended
            }
        }

        log.info("Found [${filteredEntries.size()}] ViewSymbolExtended with min/max [${minQuotePrice}, ${maxQuotePrice}] in [${System.currentTimeMillis() - startStopwatch} ms] ")

        filteredEntries
    }
}
