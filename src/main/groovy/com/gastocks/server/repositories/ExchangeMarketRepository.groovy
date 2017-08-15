package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableExchangeMarket
import org.springframework.data.repository.CrudRepository

interface ExchangeMarketRepository extends CrudRepository<PersistableExchangeMarket, String> {

    PersistableExchangeMarket findByShortName(String shortName)

}