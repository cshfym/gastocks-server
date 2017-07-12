package com.gastocks.server.models.domain.jms

class QueueableQuote {

    String symbol

    @Override
    String toString() {
        String.format("QueueableQuote [%s]", symbol)
    }
}
