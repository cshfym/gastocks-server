package com.gastocks.server.jms.services.simulation.technical

import com.gastocks.server.models.technical.response.TechnicalQuote
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class RSIIndicatorService {

    boolean getRSIBuyIndicator(TechnicalQuote quote) {

        // RSI is trending upward, out of overbought territory, signaling a potential BUY action
        if (quote.rsiParameters.overBoughtCrossoverPositive) { return true }

        false
    }

    boolean getRSISellIndicator(TechnicalQuote quote) {

        // RSI is trending downward, out of oversold territory, signaling a potential SELL action
        if (quote.rsiParameters.overSoldCrossoverNegative) { return true }

        false

    }
}
