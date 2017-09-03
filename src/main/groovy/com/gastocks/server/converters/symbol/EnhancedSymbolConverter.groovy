package com.gastocks.server.converters.symbol

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.PersistableSymbolExtended
import com.gastocks.server.models.symbol.EnhancedSymbol
import org.springframework.stereotype.Component

@Component
class EnhancedSymbolConverter {

    EnhancedSymbol fromPersistableSymbol(PersistableSymbol persistableSymbol, List<PersistableSymbolExtended> symbolExtendedList) {

        new EnhancedSymbol(
            identifier: persistableSymbol.identifier,
            description: persistableSymbol.description,
            active: persistableSymbol.active,
            exchangeMarket: persistableSymbol.exchangeMarket.shortName,
            maximumQuotePrice: (symbolExtendedList) ? symbolExtendedList.max { it.price }.price : 0.0d,
            minimumQuotePrice: (symbolExtendedList) ? symbolExtendedList.min { it.price }.price : 0.0d,
            averageQuotePrice: (symbolExtendedList) ? (double)(symbolExtendedList.sum { it.price } / symbolExtendedList.size()).round(2) : 0.0d,
            quotePeriods: (symbolExtendedList) ? symbolExtendedList.size() : 0,
            newestQuoteDate: (symbolExtendedList) ? symbolExtendedList[0].quoteDate : null,
            oldestQuoteDate: (symbolExtendedList) ? symbolExtendedList[symbolExtendedList.size() - 1].quoteDate : null
        )
    }
}
