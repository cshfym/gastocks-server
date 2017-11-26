package com.gastocks.server.jms.sender

import com.gastocks.server.jms.models.SectorQuoteBackfillMessage
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Slf4j
@Service
class SectorQuoteBackfillQueueSender {

    @Autowired
    ApplicationContext applicationContext

    final static String QUEUE_SECTOR_QUOTE_BACKFILL_REQUEST = "com.gastocks.queue.sector.quote.backfill"

    /**
     * Queues a request to backfill a sector quote
     */
    void queueRequest(SectorQuoteBackfillMessage message) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        log.info "Queueing a sector quote backfill request for processing: [${SectorQuoteBackfillMessage.toString()}]"

        jmsTemplate.convertAndSend(QUEUE_SECTOR_QUOTE_BACKFILL_REQUEST, message)
    }

}
