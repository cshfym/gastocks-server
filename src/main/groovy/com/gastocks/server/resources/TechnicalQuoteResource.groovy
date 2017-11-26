package com.gastocks.server.resources

import com.gastocks.server.jms.models.SectorQuoteBackfillMessage
import com.gastocks.server.jms.sender.SectorQuoteBackfillQueueSender
import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.constants.GlobalConstants
import com.gastocks.server.models.domain.PersistableSectorPerformance
import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.technical.request.TechnicalQuoteRequestParameters
import com.gastocks.server.models.technical.response.TechnicalQuote
import com.gastocks.server.schedulers.SectorQuoteBackfillScheduledTask
import com.gastocks.server.services.technical.TechnicalQuoteService
import com.gastocks.server.services.technical.TechnicalSectorQuoteService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/technicalquote")
class TechnicalQuoteResource {

    @Autowired
    TechnicalQuoteService quoteService

    @Autowired
    TechnicalSectorQuoteService technicalSectorQuoteService

    @Autowired
    SectorQuoteBackfillQueueSender queueSender

    @Autowired
    SectorQuoteBackfillScheduledTask sectorQuoteBackfillScheduledTask

    @ResponseBody
    @RequestMapping(value="/{symbol}", method=RequestMethod.POST)
    List<TechnicalQuote> getTechnicalQuote(@PathVariable("symbol") String symbol, @RequestBody TechnicalQuoteRequestParameters requestParameters) {

        def quotes

        try {
            quotes = quoteService.getTechnicalQuotesForSymbol(symbol, requestParameters)
        } catch (QuoteNotFoundException ex) {
            log.info "Quote not found for symbol [${symbol}]"
            throw ex
        } catch (Exception ex) {
            log.error("Exception during call to get technical quote for [${symbol}]: ", ex)
            throw ex
        }

        quotes
    }

    @ResponseBody
    @RequestMapping(value="/sector/{sector}", method=RequestMethod.GET)
    List<PersistableSectorPerformance> getTechnicalQuoteForSector(@PathVariable("sector") String sector) {
        technicalSectorQuoteService.getTechnicalQuoteForSector(sector)
    }

    @ResponseBody
    @RequestMapping(value="/sector/{sector}/{date}", method=RequestMethod.POST)
    BasicResponse queueSectorPerformanceBackfillByDate(@PathVariable("sector") String sector, @PathVariable("date") String quoteDate) {
        queueSender.queueRequest(
            new SectorQuoteBackfillMessage(
                sector: sector,
                date: GlobalConstants.SHORT_DATE_FORMAT.parse(quoteDate))
        )
        new BasicResponse(success: true)
    }

    /**
     * Convenience method to trigger the scheduled cron.
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/sector/backfill", method=RequestMethod.POST)
    BasicResponse doBackfillSectorsWithScheduledTask() {
        sectorQuoteBackfillScheduledTask.process()
        new BasicResponse(success: true)
    }
}