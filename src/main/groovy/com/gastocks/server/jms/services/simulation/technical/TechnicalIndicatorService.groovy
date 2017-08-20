package com.gastocks.server.jms.services.simulation.technical

import com.gastocks.server.models.simulation.MACDRequestParameters
import com.gastocks.server.models.technical.TechnicalQuote
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class TechnicalIndicatorService {


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
