package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableQuoteAudit
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.repositories.QuoteAuditRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Service layer for quote-related persistence operations.
 */
@Slf4j
@Service
class QuoteAuditPersistenceService {

    @Autowired
    QuoteAuditRepository quoteAuditRepository

    void persistNewQuoteAudit(PersistableSymbol symbol, PersistableQuote quote, String auditText) {

        def persistableQuoteAudit = new PersistableQuoteAudit(
                symbol: symbol,
                quote: quote,
                auditText: auditText)

        log.debug("Saving PersistableQuoteAudit: ${persistableQuoteAudit.toString()}")

        quoteAuditRepository.save(persistableQuoteAudit)
    }

    List<PersistableQuoteAudit> findAll() {
        quoteAuditRepository.findAll()
    }

    PersistableQuoteAudit findAllBySymbol(PersistableSymbol symbol) {
        quoteAuditRepository.findBySymbol(symbol)
    }

    // TODO This could probably be improved by using a single HQL statement
    @Transactional
    void removeAllAudits() {

        def startStopwatch = System.currentTimeMillis()
        int count = 0

        log.info("Removing ALL entities for QueueAudit")

        List<PersistableQuoteAudit> allAudits = quoteAuditRepository.findAll()
        allAudits.each { audit ->
            quoteAuditRepository.delete(audit)
            count++
        }

        log.info("ALL [${count}] QueueAudit entities removed in [${System.currentTimeMillis() - startStopwatch} ms]")
    }

}
