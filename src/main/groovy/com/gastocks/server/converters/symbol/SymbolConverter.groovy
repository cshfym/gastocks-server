package com.gastocks.server.converters.symbol

import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.symbol.Symbol
import org.springframework.stereotype.Component

@Component
class SymbolConverter {

    static Symbol fromPersistableSymbol(PersistableSymbol persistableSymbol) {

        new Symbol(
            identifier: persistableSymbol.identifier,
            description: persistableSymbol.description,
            active: persistableSymbol.active,
            exchangeMarket: persistableSymbol.exchangeMarket.shortName
        )
    }
}
