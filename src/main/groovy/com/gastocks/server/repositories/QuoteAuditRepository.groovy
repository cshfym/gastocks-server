package com.gastocks.server.repositories

import com.gastocks.server.models.domain.PersistableQuoteAudit
import com.gastocks.server.models.domain.PersistableSymbol
import org.springframework.data.repository.CrudRepository

interface QuoteAuditRepository extends CrudRepository<PersistableQuoteAudit, String> {

    PersistableQuoteAudit findBySymbol(PersistableSymbol symbol)

    void deleteBySymbol(PersistableSymbol symbol)
}