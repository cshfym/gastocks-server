package com.gastocks.server.jms.services.simulation.technical

import com.gastocks.server.models.technical.response.TechnicalQuote
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class RSIIndicatorService {

    /*
        Observations:
        1. 80/20 outperforms 70/30 on stocks $8-$15
        2. 80/20 RSI crossovers with corresponding MACD crossover (within a few days) seem to pair well
        3.
     */

    static boolean getRSIBuyIndicator(TechnicalQuote quote) {

        // RSI is trending upward, out of overbought territory, signaling a potential BUY action
        if (quote.rsiParameters.overSoldCrossoverPositive) { return true }

        false
    }

    static boolean getRSISellIndicator(TechnicalQuote quote) {

        // RSI is trending downward, out of oversold territory, signaling a potential SELL action
        if (quote.rsiParameters.overBoughtCrossoverNegative) { return true }

        false

    }
}
