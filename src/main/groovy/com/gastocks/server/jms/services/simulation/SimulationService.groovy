package com.gastocks.server.jms.services.simulation

import com.gastocks.server.models.technical.TechnicalQuote
import com.gastocks.server.models.simulation.BasicSimulation
import com.gastocks.server.models.simulation.SimulationRequest
import com.gastocks.server.models.simulation.SimulationSummary
import com.gastocks.server.models.simulation.StockTransaction
import com.gastocks.server.models.symbol.Symbol
import com.gastocks.server.services.TechnicalQuoteService
import com.gastocks.server.services.SymbolService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class SimulationService {

    @Autowired
    TechnicalQuoteService emaQuoteService

    @Autowired
    SymbolService symbolService

    // final static String CSV_HEADER_ROW = '"Symbol","Total Investment","Total Earnings Percentage","Net Proceeds","Gross Proceeds","Total Commission Cost","Transaction Count"'

    void doSimulationWithRequest(SimulationRequest request) {

        def startStopwatch = System.currentTimeMillis()

        List<SimulationSummary> summaryList = []

        List<Symbol> allSymbols = symbolService.findAllSymbols()

        List<Symbol> filteredSymbols = []
        allSymbols.each { symbol ->
            if (request.symbols) {
                if (request.symbols.contains(symbol.identifier)) {
                    filteredSymbols << symbol
                }
            } else {
                filteredSymbols << symbol
            }
        }

        filteredSymbols.eachWithIndex { symbol, ix ->
            if (ix > 99) { return }
            if (request.symbols && (!request.symbols.contains(symbol.identifier))) { return } // Only process requested symbols, if specified.

            List<TechnicalQuote> quotes = emaQuoteService.getTechnicalQuotesForSymbol(symbol.identifier, request) // Sloppy - re-retrieves symbol in this method.
            def simulation = doSimulationForSymbol(quotes, symbol.identifier, request)
            if (simulation) {
                summaryList << simulation
            }
        }

        log.info("*** Symbol Simulation Complete in [${System.currentTimeMillis() - startStopwatch} ms]")
        log.info("*** Simulation: [${request.description}]")
        summaryList.each { summary ->
            log.info(summary.toString())
        }
    }

    SimulationSummary doSimulationForSymbol(List<TechnicalQuote> quotes, String symbol, SimulationRequest request) {

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

            if (ix > 0) {

                // Initiate a BUY action
                if (quote.signalCrossoverPositive) {
                    // If aboveCenter parameter, only initiate BUY if signal line is > 0
                    if (!request.macdParameters.macdPositiveTrigger || (request.macdParameters.macdPositiveTrigger && (quote.macdSignalLine >= 0.0))) {
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
                    stockTransaction = new StockTransaction(shares: request.shares, symbol: symbol)
                }
            }
        }

        simulation.summary
    }

    /*
    SimulationSummary doSimulationForSymbol(String symbol, int emaShort, int emaLong, boolean aboveCenter) {

        List<TechnicalQuote> quotes = emaQuoteService.getTechnicalQuotesForSymbol(symbol, emaShort, emaLong)

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
