package com.gastocks.server.resources

import com.gastocks.server.models.BasicQuoteResponse
import com.gastocks.server.models.domain.jms.QueueableQuote
import com.gastocks.server.services.avtimeseriesadjusted.AVTimeSeriesAdjustedFetchAndPersistService
import com.gastocks.server.services.avtimeseriesadjusted.AVTimeSeriesAdjustedQuoteService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Slf4j
@Controller
@RequestMapping("/avtsa")
class AVTimeSeriesAdjustedQuoteResource {

    @Autowired
    AVTimeSeriesAdjustedQuoteService quoteService

    @Autowired
    AVTimeSeriesAdjustedFetchAndPersistService fetchAndPersistService

    @ResponseBody
    @RequestMapping(value="/quote", method=RequestMethod.GET)
    BasicQuoteResponse getQuote(@RequestParam(value="symbol", required=true) String symbol) {

        def quote = quoteService.getQuote(symbol)

        if (quote) {
            new BasicQuoteResponse(
                success: true,
                    message: "",
                quote: quote)
        } else {
            new BasicQuoteResponse(success: false, message: "Not found")
        }

    }

    @ResponseBody
    @RequestMapping(value="/batch", method=RequestMethod.GET)
    BasicQuoteResponse doSymbol(@RequestParam(value="symbol", required=true) String symbol) {

        fetchAndPersistService.fetchAndPersistQuote(symbol)

        new BasicQuoteResponse(success: true, message: "")
    }

    @ResponseBody
    @RequestMapping(value="/partial", method=RequestMethod.GET)
    BasicQuoteResponse doPartial(@RequestParam(value="symbol", required=true) String symbol) {

       fetchAndPersistService.fetchAndPersistQuotesPartial(symbol)

        new BasicQuoteResponse(success: true, message: "")
    }

    @ResponseBody
    @RequestMapping(value="batchAll", method=RequestMethod.GET)
    BasicQuoteResponse doBatchAll() {

        fetchAndPersistService.fetchAndPersistAllQuotes()

        new BasicQuoteResponse(success: true, message: "")
    }

    @Autowired
    ApplicationContext applicationContext

    @ResponseBody
    @RequestMapping(value="/queue", method=RequestMethod.GET)
    BasicQuoteResponse doQueueSymbol(@RequestParam(value="symbol", required=true) String symbol) {

        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class)

        def queueObject = new QueueableQuote(symbol: symbol)

        log.info "Queueing a symbol for processing: <{ ${queueObject} }>"
        jmsTemplate.convertAndSend("quote_queue", queueObject)
    }
}