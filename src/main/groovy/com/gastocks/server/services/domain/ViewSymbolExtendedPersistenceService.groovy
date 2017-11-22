package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.ViewSymbolExtended
import com.gastocks.server.models.vse.VSERequestParameters
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

    List<ViewSymbolExtended> findAllWithParameters(VSERequestParameters parameters) {

        def startStopwatch = System.currentTimeMillis()

        def filteredEntries = []
        def allEntries = viewSymbolExtendedCacheService.findAllViewSymbolExtendedFromCache()

        boolean quoteMeetsFilterCriteria = true
        allEntries.each { viewSymbolExtended ->

            if (!quoteMeetsMinMaxParameters(parameters, viewSymbolExtended)) { quoteMeetsFilterCriteria = false }
            if (parameters.sector && (parameters.sector != viewSymbolExtended.sector)) { quoteMeetsFilterCriteria = false }
            if (parameters.industryCategory && (parameters.industryCategory != viewSymbolExtended.industryCategory)) { quoteMeetsFilterCriteria = false }
            if (parameters.industrySubCategory && (parameters.industrySubCategory != viewSymbolExtended.industrySubCategory)) { quoteMeetsFilterCriteria = false }

            if (quoteMeetsFilterCriteria) {
                filteredEntries << viewSymbolExtended
            }
        }

        log.info("Found [${filteredEntries.size()}] ViewSymbolExtended with min/max [${parameters.minQuotePrice}, ${parameters.maxQuotePrice}] " +
                "in [${System.currentTimeMillis() - startStopwatch} ms] ")

        filteredEntries
    }

    boolean quoteMeetsMinMaxParameters(VSERequestParameters parameters, ViewSymbolExtended viewSymbolExtended) {
        (viewSymbolExtended.maxPrice <= parameters.maxQuotePrice) && (viewSymbolExtended.minPrice >= parameters.minQuotePrice)
    }
}
