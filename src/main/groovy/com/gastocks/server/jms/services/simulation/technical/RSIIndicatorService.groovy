package com.gastocks.server.jms.services.simulation.technical

import com.gastocks.server.models.technical.request.RSIRequestParameters
import com.gastocks.server.models.technical.response.TechnicalQuote
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class RSIIndicatorService {

    boolean getRSIBuyIndicator(TechnicalQuote quote, RSIRequestParameters requestParameters, int index) {

        if (index > 0 && quote.rsiParameters.relativeStrengthIndex) {

        }

        false
    }

}
