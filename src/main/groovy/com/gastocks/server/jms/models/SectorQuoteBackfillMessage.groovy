package com.gastocks.server.jms.models

class SectorQuoteBackfillMessage {

    String sector
    Date date

    @Override
    String toString() {
        "SectorQuoteBackfillMessage: Sector ID [${sector}] for date [${date.toString()}]"
    }
}
