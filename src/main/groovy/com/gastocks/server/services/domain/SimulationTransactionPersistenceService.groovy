package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableSimulation
import com.gastocks.server.models.domain.PersistableSimulationTransaction
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.repositories.SimulationRepository
import com.gastocks.server.repositories.SimulationTransactionRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class SimulationTransactionPersistenceService {

    @Autowired
    SimulationTransactionRepository transactionRepository

    List<PersistableSimulationTransaction> findAllBySimulationAndSymbol(PersistableSimulation persistableSimulation, PersistableSymbol symbol) {
        transactionRepository.findAllBySimulationAndSymbol(persistableSimulation, symbol)
    }

    List<PersistableSimulationTransaction> findAllBySimulation(PersistableSimulation persistableSimulation) {
        transactionRepository.findAllBySimulation(persistableSimulation)
    }

    @Transactional
    PersistableSimulationTransaction persistNewSimulationTransaction(PersistableSimulation simulation,
         PersistableSymbol symbol, int shares, double commission, double purchasePrice, double sellPrice, Date purchaseDate, Date sellDate) {

        def transaction = new PersistableSimulationTransaction(
            simulation: simulation,
            symbol: symbol,
            shares: shares,
            commission: commission,
            purchasePrice: purchasePrice,
            sellPrice: sellPrice,
            purchaseDate: purchaseDate,
            sellDate: sellDate,
            createDate: new Date()
        )

        log.debug("Saving simulation transaction: ${transaction.toString()}")

        transactionRepository.save(transaction)
    }
}
