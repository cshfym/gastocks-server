package com.gastocks.server.services.simulation

import com.gastocks.server.models.quote.EMAQuote
import com.gastocks.server.models.simulation.BasicSimulation
import com.gastocks.server.models.simulation.StockTransaction
import com.gastocks.server.services.EMAQuoteService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class SimulationService {

    @Autowired
    EMAQuoteService emaQuoteService

    BasicSimulation doSimulationForSymbol(String symbol, int emaShort, int emaLong, boolean aboveCenter) {

        List<EMAQuote> quotes = emaQuoteService.getEMAQuotesForSymbol(symbol, emaShort, emaLong)

        quotes.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate }

        BasicSimulation simulation = new BasicSimulation(stockTransactions: [])

        // Establish starting transaction
        StockTransaction stockTransaction = new StockTransaction(shares: 100)

        // Iterate each quote ascending, examining and acting on buy/sell signals
        quotes.eachWithIndex { quote, ix ->

            if (ix > 0) {

                // Initiate a BUY action
                if (quote.signalCrossoverPositive) {
                    if (!aboveCenter || (aboveCenter && (quote.macdSignalLine >= 0.0))) { // If aboveCenter parameter, only initiate BUY if signal line is > 0
                        log.info("Initiating BUY action with MACD at [${quote.macd}], signal [${quote.macdSignalLine}], MACDHist [${quote.macdHist}]")
                        stockTransaction.purchaseDate = quote.quoteDate
                        stockTransaction.purchasePrice = quote.price
                    }
                }

                // Initiate a SELL action, save transaction and reset
                if (quote.signalCrossoverNegative && stockTransaction.started) {
                    log.info("Initiating SELL action with MACD at [${quote.macd}], signal [${quote.macdSignalLine}], MACDHist [${quote.macdHist}]")
                    stockTransaction.sellDate = quote.quoteDate
                    stockTransaction.sellPrice = quote.price
                    simulation.stockTransactions << stockTransaction
                    stockTransaction = new StockTransaction(shares: 100)
                }
            }
        }

        log.info("*** Simulation Complete ***")
        log.info("Transactions generated for [${symbol}]: [${simulation.stockTransactions.size()}]")
        simulation.stockTransactions.each { transaction ->
            log.info(transaction.toString())
        }
        log.info("Simulation gross proceeds: [${simulation.grossProceeds}], net proceeds: [${simulation.netProceeds}]")

        simulation
    }


}
