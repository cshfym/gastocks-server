package com.gastocks.server.resources

import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.intrinio.IntrinioExchangeRequest
import com.gastocks.server.services.intrinio.exchangeprices.ExchangePriceApiService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/intrinio/exchange")
class IntrinioExchangeResource {

    @Autowired
    ExchangePriceApiService exchangePriceApiService

    /**
     * Convenience API to trigger load of exchange price data from Intrinio
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/pricebackfill", method=RequestMethod.POST)
    BasicResponse backfill(@RequestBody IntrinioExchangeRequest request) {
        exchangePriceApiService.backfill(request)
        new BasicResponse(success: true)
    }

}