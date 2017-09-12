package com.gastocks.server.jms.services.simulation

import com.gastocks.server.jms.services.simulation.technical.TechnicalIndicatorService
import com.gastocks.server.models.domain.PersistableSimulation
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.jms.QueueableSimulationSymbol
import com.gastocks.server.models.technical.TechnicalQuote
import com.gastocks.server.models.simulation.SymbolSimulation
import com.gastocks.server.models.simulation.SimulationRequest
import com.gastocks.server.models.simulation.TemporarySimulationTransaction
import com.gastocks.server.services.TechnicalQuoteService
import com.gastocks.server.services.domain.SimulationPersistenceService
import com.gastocks.server.services.domain.SimulationTransactionPersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class SimulationService {

    @Autowired
    SimulationPersistenceService simulationPersistenceService

    @Autowired
    SimulationTransactionPersistenceService transactionPersistenceService

    @Autowired
    TechnicalQuoteService technicalQuoteService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    TechnicalIndicatorService technicalIndicatorService

    /**
     * Receives the incoming symbol payload, finds the original simulation wrapper, and runs a simulation
     * on all all technical quote data.
     * @param simulationSymbol
     */
    void doSimulationWithRequest(QueueableSimulationSymbol simulationSymbol) {

        def startStopwatch = System.currentTimeMillis()

        PersistableSimulation persistableSimulation = simulationPersistenceService.findById(simulationSymbol.simulationId)
        if (! persistableSimulation) {
            log.warn("In doSimulationWithRequest and could not find simulation with ID [${simulationSymbol.simulationId}]")
            return
        }

        PersistableSymbol persistableSymbol = symbolPersistenceService.findByIdentifier(simulationSymbol.symbol)

        // Reconstitute request from simulation attributes.
        def jsonSlurper = new JsonSlurper()
        SimulationRequest simulationRequest = jsonSlurper.parseText(persistableSimulation.attributes) as SimulationRequest

        List<TechnicalQuote> quotes = technicalQuoteService.getTechnicalQuotesForSymbol(simulationSymbol.symbol, simulationRequest)
        SymbolSimulation simulation = doSimulationForSymbol(quotes, simulationSymbol.symbol, simulationRequest)

        if (simulation) {
            log.info("*** Symbol Simulation Complete in [${System.currentTimeMillis() - startStopwatch} ms]")
            log.info("*** Simulation: [${persistableSimulation.attributes}]")

            simulation.stockTransactions?.each { TemporarySimulationTransaction transaction ->
                transactionPersistenceService.persistNewSimulationTransaction(
                        persistableSimulation, persistableSymbol, transaction.shares, transaction.commission, transaction.purchasePrice,
                        transaction.sellPrice, transaction.purchaseDate, transaction.sellDate)
            }
        }
    }

    /**
     * Primary method for initiating the simulation for the specified symbol.
     * @param quotes
     * @param symbol
     * @param request
     * @return {@link SymbolSimulation}
     */
    SymbolSimulation doSimulationForSymbol(List<TechnicalQuote> quotes, String symbol, SimulationRequest request) {

        log.info ("Starting simulation for symbol [${symbol}]")

        if (!quotes) {
            log.info("No quotes loaded for symbol [${symbol}], bypassing for simulation.")
            return null
        }

        quotes.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate }

        SymbolSimulation simulation = new SymbolSimulation(symbol: symbol, stockTransactions: [])

        /**
         * Establish the "session" min/max purchase price - once the first purchase transaction occurs, we essentially
         * establish a much higher limit so as not to prevent subsequent transactions from being capped at the initial max price.
         */
        double sessionMaxPurchasePrice = request.maxPurchasePrice ?: 99999.00d
        double sessionMinPurchasePrice = request.minPurchasePrice ?: 0.0d

        // Establish starting transaction
        TemporarySimulationTransaction stockTransaction = new TemporarySimulationTransaction(shares: request.shares, symbol: symbol, commission: request.commissionPrice)

        // Iterate each quote ascending, examining and acting on buy/sell signals
        quotes.eachWithIndex { quote, ix ->

            if (!stockTransaction.started) {

                if ((sessionMaxPurchasePrice > 0.0d) && (quote.price > sessionMaxPurchasePrice)) { return }
                if ((sessionMinPurchasePrice > 0.0d) && (quote.price < sessionMinPurchasePrice)) { return }

                boolean buyIndicator = technicalIndicatorService.getMACDBuyIndicator(quote, request.macdParameters, ix)

                if ((request.onlyTransactOnPriceChange) && (!quote.priceChangeFromLastQuote)) { buyIndicator = false }

                // Add future indicators here, inspect all indicators before buying

                if (buyIndicator) {
                    //log.info("Initiating BUY action with MACD at [${quote.macd}], signal [${quote.macdSignalLine}], MACDHist [${quote.macdHist}]")
                    stockTransaction.purchaseDate = quote.quoteDate
                    stockTransaction.purchasePrice = quote.price
                    sessionMaxPurchasePrice = 9999999.00d
                }
            }

            boolean sellIndicator = technicalIndicatorService.getMACDSellIndicator(quote)
            // Add future indicators here, inspect all indicators before selling

            if (sellIndicator && stockTransaction.started) {
                // log.info("Initiating SELL action with MACD at [${quote.macd}], signal [${quote.macdSignalLine}], MACDHist [${quote.macdHist}]")
                stockTransaction.sellDate = quote.quoteDate
                stockTransaction.sellPrice = quote.price
                simulation.stockTransactions << stockTransaction
                stockTransaction = new TemporarySimulationTransaction(shares: request.shares, symbol: symbol, commission: request.commissionPrice)
            }
        }

        // Close out any purchased and un-sold stock.
        if (request.sellOpenPositions && (stockTransaction.started && stockTransaction.purchaseDate && !stockTransaction.sellDate)) {
            stockTransaction.sellDate = quotes.last().quoteDate
            stockTransaction.sellPrice = quotes.last().price
            simulation.stockTransactions << stockTransaction
        }

        simulation
    }

}
