package com.gastocks.server.models.domain.jms

class QueueableSymbol {

    String symbolId
    String identifier

    @Override
    String toString() {
        String.format("QueueableSymbol [%s]", identifier)
    }
}
