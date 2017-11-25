package com.gastocks.server.schedulers

import com.gastocks.server.models.constants.GlobalConstants
import com.gastocks.server.models.intrinio.IntrinioExchangeRequest
import com.gastocks.server.services.intrinio.exchangeprices.ExchangePriceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class IntrinioPriceExchangeScheduledTask {

    @Autowired
    ExchangePriceService exchangePriceService

    @Value('${intrinio.api.exchangeprice.daily.enabled}')
    boolean dailyServiceEnabled

    @Scheduled(cron = '${intrinio.api.exchangeprice.daily.schedule}')
    void process() {

        if (!dailyServiceEnabled) { return }

        String today = GlobalConstants.SHORT_DATE_FORMAT.format(new Date())

        // NASDAQ
        exchangePriceService.fetchAndPersistExchangePrices(
            new IntrinioExchangeRequest(
                exchange: ExchangePriceService.NASDAQ_EXCHANGE,
                date: today,
                startPage: 1,
                endPage: 99
            )
        )

        // NYSE
        exchangePriceService.fetchAndPersistExchangePrices(
            new IntrinioExchangeRequest(
                exchange: ExchangePriceService.NYSE_EXCHANGE,
                date: today,
                startPage: 1,
                endPage: 99
            )
        )

    }
}
