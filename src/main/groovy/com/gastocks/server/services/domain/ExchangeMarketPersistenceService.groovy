package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableExchangeMarket
import com.gastocks.server.repositories.ExchangeMarketRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class ExchangeMarketPersistenceService {

    @Autowired
    ExchangeMarketRepository exchangeMarketRepository

    PersistableExchangeMarket findByShortName(String name) {
        exchangeMarketRepository.findByShortName(name)
    }

}
