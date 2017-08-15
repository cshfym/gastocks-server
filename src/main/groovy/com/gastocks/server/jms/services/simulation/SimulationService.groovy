package com.gastocks.server.jms.services.simulation

import com.gastocks.server.models.domain.PersistableSimulation
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.jms.QueueableSimulationSymbol
import com.gastocks.server.models.simulation.MACDRequestParameters
import com.gastocks.server.models.technical.TechnicalQuote
import com.gastocks.server.models.simulation.BasicSimulation
import com.gastocks.server.models.simulation.SimulationRequest
import com.gastocks.server.models.simulation.SimulationSummary
import com.gastocks.server.models.simulation.StockTransaction
import com.gastocks.server.models.symbol.Symbol
import com.gastocks.server.services.TechnicalQuoteService
import com.gastocks.server.services.SymbolService
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
    SymbolService symbolService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    void doSimulationWithRequest(QueueableSimulationSymbol simulationSymbol) {

        def startStopwatch = System.currentTimeMillis()

        PersistableSimulation persistableSimulation = simulationPersistenceService.findById(simulationSymbol.simulationId)
        PersistableSymbol persistableSymbol = symbolPersistenceService.findByIdentifier(simulationSymbol.symbol)

        // Reconstitute request from simulation attributes.
        def jsonSlurper = new JsonSlurper()
        SimulationRequest simulationRequest = jsonSlurper.parseText(persistableSimulation.attributes)

        List<TechnicalQuote> quotes = technicalQuoteService.getTechnicalQuotesForSymbol(simulationSymbol.symbol, simulationRequest)
        BasicSimulation simulation = doSimulationForSymbol(quotes, simulationSymbol.symbol, simulationRequest)

        log.info("*** Symbol Simulation Complete in [${System.currentTimeMillis() - startStopwatch} ms]")
        log.info("*** Simulation: [${persistableSimulation.attributes}]")

        simulation.stockTransactions.each { StockTransaction transaction ->
            transactionPersistenceService.persistNewSimulationTransaction(
                persistableSimulation, persistableSymbol, transaction.shares, transaction.commission, transaction.purchasePrice,
                transaction.sellPrice, transaction.purchaseDate, transaction.sellDate)
        }
    }

    BasicSimulation doSimulationForSymbol(List<TechnicalQuote> quotes, String symbol, SimulationRequest request) {

        log.info ("Starting simulation for symbol [${symbol}]")

        if (!quotes) {
            log.info("No quotes loaded for symbol [${symbol}], bypassing for simulation.")
            return null
        }

        quotes.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate }

        BasicSimulation simulation = new BasicSimulation(symbol: symbol, stockTransactions: [])

        // Establish starting transaction
        StockTransaction stockTransaction = new StockTransaction(shares: request.shares, symbol: symbol)

        // Iterate each quote ascending, examining and acting on buy/sell signals
        quotes.eachWithIndex { quote, ix ->

            boolean buyIndicator = getMACDBuyIndicator(quote, request.macdParameters, ix)
            // Add future indicators here, inspect all indicators before buying

            if (buyIndicator) {
                //log.info("Initiating BUY action with MACD at [${quote.macd}], signal [${quote.macdSignalLine}], MACDHist [${quote.macdHist}]")
                stockTransaction.purchaseDate = quote.quoteDate
                stockTransaction.purchasePrice = quote.price
            }

            boolean sellIndicator = getMACDSellIndicator(quote)
            // Add future indicators here, inspect all indicators before selling

            if (sellIndicator && stockTransaction.started) {
                // log.info("Initiating SELL action with MACD at [${quote.macd}], signal [${quote.macdSignalLine}], MACDHist [${quote.macdHist}]")
                stockTransaction.sellDate = quote.quoteDate
                stockTransaction.sellPrice = quote.price
                simulation.stockTransactions << stockTransaction
                stockTransaction = new StockTransaction(shares: request.shares, symbol: symbol)
            }
        }

        simulation
    }

    boolean getMACDBuyIndicator(TechnicalQuote quote, MACDRequestParameters requestParameters, int index) {

        if (index > 0 && quote.signalCrossoverPositive) {
            // If aboveCenter parameter, only initiate BUY if signal line is > 0
            if (!requestParameters.macdPositiveTrigger || (requestParameters.macdPositiveTrigger && (quote.macdSignalLine >= 0.0))) {
                return true
            }
        }

        false
    }

    boolean getMACDSellIndicator(TechnicalQuote quote) {
        quote.signalCrossoverNegative
    }

}
