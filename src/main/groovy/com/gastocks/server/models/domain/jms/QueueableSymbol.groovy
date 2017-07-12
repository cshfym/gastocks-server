package com.gastocks.server.models.domain.jms

class QueueableSymbol {

    String id
    String identifier

    @Override
    String toString() {
        String.format("QueueableSymbol [%s]", identifier)
    }
}
