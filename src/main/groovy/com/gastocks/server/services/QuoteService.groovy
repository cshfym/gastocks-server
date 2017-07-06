package com.gastocks.server.services

import com.gastocks.server.converters.QuoteConverter
import com.gastocks.server.models.Quote
import groovy.json.JsonSlurper
import groovy.json.StreamingJsonBuilder
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
//import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

@Service
@Slf4j
@CompileStatic
class QuoteService {

    private static final String apiKey = "W2OXJLZJ9W0O5K1M"

    Quote getQuote(String symbol) {

        /**
         // https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=MSFT&apikey=demo
         Quote quote = restTemplate.getForObject(alphaVantageGlobalQuoteSymbolUri + symbol + apiKeyParam, Quote.class)
         **/

        def startStopwatch = 0
        def finishHttpRequest = 0
        def finishQuote = 0

         Quote quote = new Quote()

        try {
            startStopwatch = System.currentTimeMillis()

            URL url = new URL("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=${symbol}&apikey=${apiKey}")
            HttpURLConnection conn = (HttpURLConnection) url.openConnection()
            conn.setRequestMethod("GET")
            conn.setRequestProperty("Accept", "application/json")

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode())
            }

            def slurped = new JsonSlurper().parse(url)

            finishHttpRequest = System.currentTimeMillis() - startStopwatch

            if (!QuoteConverter.hasData(slurped)) {
                log.warn("No quote data found for symbol [${symbol}]")
                return new Quote(symbol: symbol)
            }

            quote = QuoteConverter.from(slurped)

            println "[${slurped}]"
            println "[${quote}]"

            conn.disconnect()

        } catch (MalformedURLException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        finishQuote = System.currentTimeMillis() - startStopwatch

        log.info "Quote retrieved in [${finishQuote}] time; [${finishHttpRequest}] of which was HTTP request"
        quote
    }


}
