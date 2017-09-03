package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.ViewSymbolExtended
import com.gastocks.server.repositories.ViewSymbolExtendedRepository
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
    ViewSymbolExtendedRepository viewSymbolExtendedRepository

    List<ViewSymbolExtended> findAllWithParameters(Double maxQuotePrice = null, Double minQuotePrice = null) {

        def startStopwatch = System.currentTimeMillis()
        def response = viewSymbolExtendedRepository.findAllWithParameters(maxQuotePrice, minQuotePrice)
        log.info("Found [${response.size()}] ViewSymbolExtended with min/max [${minQuotePrice}, ${maxQuotePrice}] in [${System.currentTimeMillis() - startStopwatch} ms] ")

        response
    }

    ViewSymbolExtended findBySymbolId(String symbolId) {
        viewSymbolExtendedRepository.findBySymbolId(symbolId)
    }

    ViewSymbolExtended findByIdentifier(String identifier) {
        viewSymbolExtendedRepository.findByIdentifier(identifier)
    }

}
