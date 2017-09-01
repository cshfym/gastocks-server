package com.gastocks.server.converters.symbol

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.symbol.EnhancedSymbol
import com.gastocks.server.models.symbol.Symbol
import org.springframework.stereotype.Component

@Component
class EnhancedSymbolConverter {

    EnhancedSymbol fromPersistableSymbol(PersistableSymbol persistableSymbol, List<Double> minMaxAvgValues) {

        new EnhancedSymbol(
            identifier: persistableSymbol.identifier,
            description: persistableSymbol.description,
            active: persistableSymbol.active,
            exchangeMarket: persistableSymbol.exchangeMarket.shortName,
            _52WeekHigh: minMaxAvgValues[0] ?: 0 ,
            _52WeekLow: minMaxAvgValues[1] ?: 0,
            _52WeekAverage: minMaxAvgValues[2] ?: 0
        )
    }
}
