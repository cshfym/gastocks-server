package com.gastocks.server.jms.services.simulation.technical

import com.gastocks.server.models.technical.request.MACDRequestParameters
import com.gastocks.server.models.technical.response.TechnicalQuote
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class MACDIndicatorService {

    static boolean getMACDBuyIndicator(TechnicalQuote quote, MACDRequestParameters requestParameters, int index) {

        if (index > 0 && quote.macdParameters.signalCrossoverPositive) {
            // If aboveCenter parameter, only initiate BUY if signal line is > 0
            if (!requestParameters.macdPositiveTrigger || (requestParameters.macdPositiveTrigger && (quote.macdParameters.macdSignalLine >= 0.0))) {
                return true
            }
        }

        false
    }

    static boolean getMACDSellIndicator(TechnicalQuote quote) {
        quote.macdParameters.signalCrossoverNegative
    }

}
