package com.gastocks.server.services

import com.gastocks.server.converters.QuoteConverter

import com.gastocks.server.models.Quote
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
@CompileStatic
class QuoteService {

    // private static final String apiKey

    Quote getQuote(String symbol) {

        /**
         // https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=MSFT&apikey=demo
         Quote quote = restTemplate.getForObject(alphaVantageGlobalQuoteSymbolUri + symbol + apiKeyParam, Quote.class)
         **/

        String apiKey = "W2OXJLZJ9W0O5K1M"

        Quote quote = new Quote()

        HttpURLConnection conn

        def startStopwatch = System.currentTimeMillis()

        try {
            URL url = new URL("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=${symbol}&apikey=${apiKey}")
            conn = (HttpURLConnection) url.openConnection()
            conn.setRequestMethod("GET")
            conn.setRequestProperty("Accept", "application/json")

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code [${conn.getResponseCode()}]")
            }

            def slurped = new JsonSlurper().parse(url)

            log.info "Slurped data for symbol [${symbol}]: [${slurped}]"

            if (!QuoteConverter.hasData(slurped)) {
                log.warn("No quote data found for symbol [${symbol}]")
                return null
            } else {
                quote = QuoteConverter.from(slurped)
                log.info "Quote: [${quote}]"
            }

        } catch (Exception ex) {
            ex.printStackTrace()
            return null
        } finally {
            conn?.disconnect()
        }

        log.info "Quote retrieved in [${System.currentTimeMillis() - startStopwatch} ms]"
        quote
    }


}
