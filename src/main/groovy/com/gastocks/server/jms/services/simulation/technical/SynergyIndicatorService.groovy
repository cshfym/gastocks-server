package com.gastocks.server.jms.services.simulation.technical

import com.gastocks.server.models.technical.response.TechnicalQuote
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class SynergyIndicatorService {

    // Check for MACD crossover within N periods of the quote
    static boolean rsiConfirmWithMacdCrossover(List<TechnicalQuote> quotes, int index, int periods, String transactionType) {

        int startIndex = (index + 1) - periods
        int endIndex = index + periods

        if (startIndex < 0) {
            startIndex = 0
            endIndex = periods * 2
        }

        if ((transactionType == "BUY") && macdSignalCrossoverPositiveInRange(quotes, startIndex, endIndex)) { return true }
        if ((transactionType == "SELL") && macdSignalCrossoverNegativeInRange(quotes, startIndex, endIndex)) { return true }

        false
    }

    static boolean macdSignalCrossoverPositiveInRange(List<TechnicalQuote> quotes, int startIndex, int endIndex) {

        for (int i = startIndex; i < endIndex; i++) {
            if (quotes[i].macdParameters.signalCrossoverPositive) {
                return true
            }
        }

        false
    }

    static boolean macdSignalCrossoverNegativeInRange(List<TechnicalQuote> quotes, int startIndex, int endIndex) {

        for (int i = startIndex; i < endIndex; i++) {
            if (quotes[i].macdParameters.signalCrossoverNegative) {
                return true
            }
        }

        false
    }

}
