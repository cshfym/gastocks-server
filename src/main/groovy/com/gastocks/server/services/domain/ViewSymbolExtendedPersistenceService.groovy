package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.ViewSymbolExtended
import com.gastocks.server.models.vse.VSERequestParameters
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

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

        allEntries.each { viewSymbolExtended ->
            if (vseMeetsFilteringCriteria(parameters, viewSymbolExtended)) {
                filteredEntries << viewSymbolExtended
            }
        }

        log.info("Found [${filteredEntries.size()}] ViewSymbolExtended with min/max [${parameters.minQuotePrice}, ${parameters.maxQuotePrice}] " +
                "in [${System.currentTimeMillis() - startStopwatch} ms] ")

        filteredEntries
    }

    boolean vseMeetsFilteringCriteria(VSERequestParameters parameters, ViewSymbolExtended viewSymbolExtended) {

        boolean vseMeetsFilteringCriteria = true

        if (!quoteMeetsMinMaxParameters(parameters, viewSymbolExtended)) { vseMeetsFilteringCriteria = false }

        if (!StringUtils.isEmpty(parameters.sector) && (parameters.sector != viewSymbolExtended.sector)) { vseMeetsFilteringCriteria = false }
        if (!StringUtils.isEmpty(parameters.industryCategory) && (parameters.industryCategory != viewSymbolExtended.industryCategory)) { vseMeetsFilteringCriteria = false }
        if (!StringUtils.isEmpty(parameters.industrySubCategory) && (parameters.industrySubCategory != viewSymbolExtended.industrySubCategory)) { vseMeetsFilteringCriteria = false }

        vseMeetsFilteringCriteria
    }

    boolean quoteMeetsMinMaxParameters(VSERequestParameters parameters, ViewSymbolExtended viewSymbolExtended) {
        (viewSymbolExtended.maxPrice <= parameters.maxQuotePrice) && (viewSymbolExtended.minPrice >= parameters.minQuotePrice)
    }
}
