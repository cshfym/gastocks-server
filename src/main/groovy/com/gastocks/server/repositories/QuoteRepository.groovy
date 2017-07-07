package com.gastocks.server.repositories

import com.gastocks.server.models.PersistableQuote
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface QuoteRepository extends CrudRepository<PersistableQuote, String> {

}