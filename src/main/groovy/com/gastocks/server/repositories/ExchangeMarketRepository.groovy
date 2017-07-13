package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableExchangeMarket
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface ExchangeMarketRepository extends CrudRepository<PersistableExchangeMarket, String> {

    PersistableExchangeMarket findByShortName(String shortName)

}