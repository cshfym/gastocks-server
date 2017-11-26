package com.gastocks.server.schedulers

import com.gastocks.server.jms.models.SectorQuoteBackfillMessage
import com.gastocks.server.jms.sender.SectorQuoteBackfillQueueSender
import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.services.domain.SectorPersistenceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class SectorQuoteBackfillScheduledTask {

    static final int MAX_QUOTE_DAYS_BACK = 5

    @Autowired
    SectorPersistenceService sectorPersistenceService

    @Autowired
    SectorQuoteBackfillQueueSender sectorQuoteBackfillQueueSender

    @Scheduled(cron = '${sector.quote.backfill.daily.schedule}')
    void process() {

        List<PersistableSector> allSectors = sectorPersistenceService.findAll()

        log.info("Loaded [${allSectors.size()}] sectors, queueing sectors for quote backfill processing.")

        List<Date> loadDates = []
        for (int i = 0; i < MAX_QUOTE_DAYS_BACK; i++) {
            loadDates << new Date() - i
        }

        // Queue all sectors and quote dates.
        allSectors.each { sector ->
            loadDates.each { quoteDate ->
                sectorQuoteBackfillQueueSender.queueRequest(new SectorQuoteBackfillMessage(sector: sector.description, date: quoteDate))
            }
        }
    }
}
