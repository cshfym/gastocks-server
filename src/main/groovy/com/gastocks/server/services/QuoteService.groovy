package com.gastocks.server.services

import com.gastocks.server.converters.IConverter
import com.gastocks.server.models.IQuote
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value

@Slf4j
abstract class QuoteService implements IQuoteService {

    abstract String API_KEY
    static final String API_KEY_PARAM = "&apikey="
    static final String OUTPUT_SIZE_PARAM = "&outputsize=full"

    abstract String getResourceUrlString()

    abstract <T extends IConverter> T getConverter()

    @Override
    public <T extends IQuote> T getQuote(String symbol) {

        T quote = null

        def startStopwatch = System.currentTimeMillis()

        HttpURLConnection conn = null

        try {
            URL url = new URL(resourceUrlString + "&symbol=${symbol}")
            conn = (HttpURLConnection) url.openConnection()
            conn.setRequestMethod("GET")
            conn.setRequestProperty("Accept", "application/json")

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code [${conn.getResponseCode()}]")
            }

            def slurped = new JsonSlurper().parse(url)

            log.trace "Slurped data at [${url.path}]for symbol [${symbol}]: [${slurped}]"

            if (!converter.hasData(slurped)) {
                log.warn("No quote data found for symbol [${symbol}]")
                return null
            } else {
                quote = converter.fromObject(slurped)
                log.trace "Constructed quote object: [${quote}]"
            }

        } catch (Exception ex) {
            ex.printStackTrace()
            return null
        } finally {
            conn?.disconnect()
        }

        log.info "Quote for symbol [${symbol}] retrieved in [${System.currentTimeMillis() - startStopwatch}] ms"

        quote
    }
}
