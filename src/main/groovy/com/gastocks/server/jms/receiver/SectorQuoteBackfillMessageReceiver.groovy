package com.gastocks.server.jms.receiver

import com.gastocks.server.jms.models.SectorQuoteBackfillMessage
import com.gastocks.server.jms.sender.SectorQuoteBackfillQueueSender
import com.gastocks.server.services.technical.TechnicalSectorQuoteService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Slf4j
@Component
class SectorQuoteBackfillMessageReceiver {

    @Autowired
    TechnicalSectorQuoteService technicalSectorQuoteService

    @JmsListener(destination = SectorQuoteBackfillQueueSender.QUEUE_SECTOR_QUOTE_BACKFILL_REQUEST, containerFactory = "sectorQuoteBackfillMessageFactory")
    void receiveSectorQuoteBackfillMessage(SectorQuoteBackfillMessage message) {

        log.debug "Received sector quote backfill [${message}] from queue ${SectorQuoteBackfillQueueSender.QUEUE_SECTOR_QUOTE_BACKFILL_REQUEST}"

        technicalSectorQuoteService.calculateAndPersistSectorPerformanceByQuoteDate(message.sector, message.date)
    }
}
