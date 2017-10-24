package com.gastocks.server.services.intrinio.exchangeprices

import com.gastocks.server.models.domain.PersistableExchangeMarket
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.intrinio.IntrinioExchangePriceQuote
import com.gastocks.server.models.intrinio.IntrinioExchangePriceResponse
import com.gastocks.server.models.intrinio.IntrinioExchangeRequest
import com.gastocks.server.services.HTTPConnectionService
import com.gastocks.server.services.domain.ExchangeMarketPersistenceService
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import com.gastocks.server.services.intrinio.IntrinioBaseService
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

import javax.transaction.Transactional
import java.text.DateFormat
import java.text.SimpleDateFormat

@Slf4j
@Service
class ExchangePriceService extends IntrinioBaseService {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    ExchangeMarketPersistenceService exchangeMarketPersistenceService

    @Autowired
    QuotePersistenceService quotePersistenceService

    @Autowired
    HTTPConnectionService connectionService

    final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")

    final static String NYSE_EXCHANGE = "^XNYS"
    final static String NASDAQ_EXCHANGE = "^XNAS"

    Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    /**
     * Typically called from JMS receiver service
     * TODO: Optimize the shit out of this.
     *  - Possibly fetch each page into memory, then dump into a queue?
     */
    void fetchAndPersistExchangePrices(IntrinioExchangeRequest request) {

        def startStopwatch = System.currentTimeMillis()

        int pageNumber = request.startPage

        String uri = INTRINIO_BASE_URL + INTRINIO_EXCHANGE_PRICE_PATH + "?identifier=${request.exchange}&price_date=${request.date}"

        String userCredentials = INTRINIO_API_KEY + ":" + INTRINIO_API_PASSWORD
        String base64EncodedCredentials = userCredentials.bytes.encodeBase64().toString()

        boolean allPagesConsumed = false

        // Resolve exchange market
        PersistableExchangeMarket exchangeMarket = null
        if (request.exchange == NYSE_EXCHANGE) { exchangeMarket = exchangeMarketPersistenceService.findByShortName("NYSE") }
        if (request.exchange == NASDAQ_EXCHANGE) { exchangeMarket = exchangeMarketPersistenceService.findByShortName("NASDAQ") }

        while(!allPagesConsumed) {

            IntrinioExchangePriceResponse priceResponse = getExchangePriceResponse(uri, base64EncodedCredentials, pageNumber)
            if (!priceResponse) {
                log.warn("No data response fetching exchange price at URI [${uri}] and page [${pageNumber}]")
                return
            }

            doHandlePriceResponse(priceResponse, exchangeMarket)

            pageNumber = priceResponse.currentPage + 1

            if (pageNumber > priceResponse.totalPages) {
                allPagesConsumed = true
            }
        }

        log.info("Completed IntrinoExchangeRequest [${request.toString()}] in [${System.currentTimeMillis() - startStopwatch} ms]")
    }

    /**
     * Fetch the exchange price response from the connection service with the URI and page specified
     * @param partialUri
     * @param base64EncodedCredentials
     * @param page
     * @return
     */
    IntrinioExchangePriceResponse getExchangePriceResponse(String partialUri, String base64EncodedCredentials, int page) {

        IntrinioExchangePriceResponse response

        String fullUri = partialUri + "&page_number=${page}"

        try {
            String exchangePricesText = connectionService.getData(fullUri, RequestMethod.GET, base64EncodedCredentials)
            response = gson.fromJson(exchangePricesText, IntrinioExchangePriceResponse.class)
        } catch (Exception ex) {
            throw ex
        }

        response
    }

    @Transactional
    void doHandlePriceResponse(IntrinioExchangePriceResponse exchangePriceResponse, PersistableExchangeMarket exchangeMarket) {

        exchangePriceResponse.data?.each { IntrinioExchangePriceQuote priceQuote ->

            Date quoteDate = SHORT_DATE_FORMAT.parse(priceQuote.date)

            PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(priceQuote.ticker)
            if (!symbol) {
                log.info("Existing ticker [${priceQuote.ticker}] not found! Persisting.")
                symbol = symbolPersistenceService.save(
                    new PersistableSymbol(
                        identifier: priceQuote.ticker,
                        description: "Created from Intrinio data feed - resolve!",
                        active: true,
                        exchangeMarket: exchangeMarket)
                )
            }

            PersistableQuote existingQuote = quotePersistenceService.findQuoteBySymbolAndQuoteDate(symbol, quoteDate)
            if (existingQuote && quotePersistenceService.quotesEqual(existingQuote, priceQuote)) {
                log.info("Bypassing quote update from IntrinioExchangePriceQuote for [${symbol.identifier}] existing quote on [${existingQuote.quoteDate}]")
            } else {
                log.info("Persisting NEW quote IntrinioExchangePriceQuote for [${symbol.identifier}] on [${quoteDate.toString()}] for [${priceQuote.close}]")
                try {
                    quotePersistenceService.persistNewQuote(priceQuote, symbol)
                } catch (Exception ex) {
                    log.warn ("Exception persisting priceQuote [${symbol.identifier}] on date [${priceQuote.date}]: ${ex.message}")
                }
            }
        }

    }
}
