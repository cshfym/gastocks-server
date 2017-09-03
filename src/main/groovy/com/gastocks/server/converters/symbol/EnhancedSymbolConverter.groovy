package com.gastocks.server.converters.symbol

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.domain.PersistableSymbolExtended
import com.gastocks.server.models.symbol.EnhancedSymbol
import org.springframework.stereotype.Component

@Component
class EnhancedSymbolConverter {

    EnhancedSymbol fromPersistableSymbol(PersistableSymbol persistableSymbol, PersistableSymbolExtended symbolExtended) {

        new EnhancedSymbol(
            identifier: persistableSymbol.identifier,
            description: persistableSymbol.description,
            active: persistableSymbol.active,
            exchangeMarket: persistableSymbol.exchangeMarket.shortName,
            maximumQuotePrice: (symbolExtended) ? symbolExtended.maximum52Weeks : 0.0d,
            minimumQuotePrice: (symbolExtended) ? symbolExtended.minimum52Weeks : 0.0d,
            averageQuotePrice: (symbolExtended) ? symbolExtended.average52Weeks : 0.0d
        )
    }
}
