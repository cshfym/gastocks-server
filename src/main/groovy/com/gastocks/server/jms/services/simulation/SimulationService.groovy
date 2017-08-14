package com.gastocks.server.jms.services.simulation

import com.gastocks.server.jms.sender.SymbolQueueSender
import com.gastocks.server.models.quote.EMAQuote
import com.gastocks.server.models.simulation.BasicSimulation
import com.gastocks.server.models.simulation.SimulationRequest
import com.gastocks.server.models.simulation.SimulationSummary
import com.gastocks.server.models.simulation.StockTransaction
import com.gastocks.server.models.symbol.Symbol
import com.gastocks.server.services.EMAQuoteService
import com.gastocks.server.services.SymbolService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class SimulationService {

    @Autowired
    EMAQuoteService emaQuoteService

    @Autowired
    SymbolService symbolService

    // final static String CSV_HEADER_ROW = '"Symbol","Total Investment","Total Earnings Percentage","Net Proceeds","Gross Proceeds","Total Commission Cost","Transaction Count"'

    void doSimulationForAllSymbols(SimulationRequest request) {

        def startStopwatch = System.currentTimeMillis()

        List<SimulationSummary> summaryList = []

        List<Symbol> allSymbols = symbolService.findAllSymbols()

        allSymbols.eachWithIndex { symbol, ix ->
            if (ix > 99) { return }
            List<EMAQuote> quotes = emaQuoteService.getEMAQuotesForSymbol(symbol.identifier, request.macdParameters) // Sloppy - re-retrieves symbol in this method.
            def simulation = doSimulationForSymbol(quotes, symbol.identifier, request.macdParameters.macdPositiveTrigger)
            if (simulation) {
                summaryList << simulation
            }
        }

        log.info("*** All Symbol Simulation Complete in [${System.currentTimeMillis() - startStopwatch} ms]")
        log.info("*** Simulation: [${request.description}]")
        summaryList.each { summary ->
            log.info(summary.toString())
        }
    }

    SimulationSummary doSimulationForSymbol(List<EMAQuote> quotes, String symbol, boolean aboveCenter) {

        log.info ("Starting simulation for symbol [${symbol}]")

        if (!quotes) {
            log.info("No quotes loaded for symbol [${symbol}], bypassing for simulation.")
            return null
        }

        quotes.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate }

        BasicSimulation simulation = new BasicSimulation(symbol: symbol, stockTransactions: [])

        // Establish starting transaction
        StockTransaction stockTransaction = new StockTransaction(shares: 100, symbol: symbol)

        // Iterate each quote ascending, examining and acting on buy/sell signals
        quotes.eachWithIndex { quote, ix ->

            if (ix > 0) {

                // Initiate a BUY action
                if (quote.signalCrossoverPositive) {
                    if (!aboveCenter || (aboveCenter && (quote.macdSignalLine >= 0.0))) { // If aboveCenter parameter, only initiate BUY if signal line is > 0
                        //log.info("Initiating BUY action with MACD at [${quote.macd}], signal [${quote.macdSignalLine}], MACDHist [${quote.macdHist}]")
                        stockTransaction.purchaseDate = quote.quoteDate
                        stockTransaction.purchasePrice = quote.price
                    }
                }

                // Initiate a SELL action, save transaction and reset
                if (quote.signalCrossoverNegative && stockTransaction.started) {
                    // log.info("Initiating SELL action with MACD at [${quote.macd}], signal [${quote.macdSignalLine}], MACDHist [${quote.macdHist}]")
                    stockTransaction.sellDate = quote.quoteDate
                    stockTransaction.sellPrice = quote.price
                    simulation.stockTransactions << stockTransaction
                    stockTransaction = new StockTransaction(shares: 100, symbol: symbol)
                }
            }
        }

        simulation.summary
    }

    /*
    SimulationSummary doSimulationForSymbol(String symbol, int emaShort, int emaLong, boolean aboveCenter) {

        List<EMAQuote> quotes = emaQuoteService.getEMAQuotesForSymbol(symbol, emaShort, emaLong)

        quotes.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate }

        BasicSimulation simulation = new BasicSimulation(symbol: symbol, stockTransactions: [])

        // Establish starting transaction
        StockTransaction stockTransaction = new StockTransaction(shares: 100)

        // Iterate each quote ascending, examining and acting on buy/sell signals
        quotes.eachWithIndex { quote, ix ->

            if (ix > 0) {

                // Initiate a BUY action
                if (quote.signalCrossoverPositive) {
                    if (!aboveCenter || (aboveCenter && (quote.macdSignalLine >= 0.0))) { // If aboveCenter parameter, only initiate BUY if signal line is > 0
                        //log.info("Initiating BUY action with MACD at [${quote.macd}], signal [${quote.macdSignalLine}], MACDHist [${quote.macdHist}]")
                        stockTransaction.purchaseDate = quote.quoteDate
                        stockTransaction.purchasePrice = quote.price
                    }
                }

                // Initiate a SELL action, save transaction and reset
                if (quote.signalCrossoverNegative && stockTransaction.started) {
                    // log.info("Initiating SELL action with MACD at [${quote.macd}], signal [${quote.macdSignalLine}], MACDHist [${quote.macdHist}]")
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

        simulation.summary
    }

    List<String> doSimulationForSymbolWithCSV(String symbol, int emaShort, int emaLong, boolean aboveCenter, boolean headerRow = true) {

        SimulationSummary summary = doSimulationForSymbol(symbol, emaShort, emaLong, aboveCenter)

        List<String> csvSummaries = []

        if (headerRow) {
            csvSummaries << CSV_HEADER_ROW
        }

        csvSummaries <<
            '"' + summary.symbol + '",' +
            summary.totalInvestment + "," +
            summary.totalEarningsPercentage + "," +
            summary.netProceeds + "," +
            summary.grossProceeds + "," +
            summary.totalCommissionCost + "," +
            summary.transactionCount

        csvSummaries
    }

    List<String> doSimulationForAllSymbolsWithCSV(int emaShort, int emaLong, boolean aboveCenter, int count) {

        List<String> csvSummaries = []
        csvSummaries << CSV_HEADER_ROW

        List<Symbol> allSymbols = symbolService.findAllSymbols()
        allSymbols.eachWithIndex { symbol, ix ->
            if (ix > count) { return }
            csvSummaries.addAll(doSimulationForSymbolWithCSV(symbol.identifier, emaShort, emaLong, aboveCenter, false))
        }

        csvSummaries
    }

    */

}
