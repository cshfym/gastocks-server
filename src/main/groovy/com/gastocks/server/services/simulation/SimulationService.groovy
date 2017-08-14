package com.gastocks.server.services.simulation

import com.gastocks.server.models.quote.EMAQuote
import com.gastocks.server.models.simulation.BasicSimulation
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

    final static String CSV_HEADER_ROW = '"Symbol","Total Investment","Total Earnings Percentage","Net Proceeds","Gross Proceeds","Total Commission Cost","Transaction Count"'

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

        /*
        log.info("*** Simulation Complete ***")
        log.info("Transactions generated for [${symbol}]: [${simulation.stockTransactions.size()}]")
        simulation.stockTransactions.each { transaction ->
            log.info(transaction.toString())
        }
        log.info("Simulation gross proceeds: [${simulation.grossProceeds}], net proceeds: [${simulation.netProceeds}]")
        */

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



}
