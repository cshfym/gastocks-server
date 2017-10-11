package com.gastocks.server.models.domain.jms

class QueueableSymbol {

    String symbolId
    String identifier

    int retryCount

    @Override
    String toString() {
        String.format("QueueableSymbol [%s] with retry count [%s]", identifier, retryCount)
    }
}
