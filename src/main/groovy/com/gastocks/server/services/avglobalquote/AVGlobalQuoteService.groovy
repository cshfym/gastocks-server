package com.gastocks.server.services.avglobalquote

import com.gastocks.server.converters.avglobalquote.AVGlobalQuoteConverter
import com.gastocks.server.models.avglobalquote.AVGlobalQuoteConstants
import com.gastocks.server.models.avglobalquote.AVGlobalQuote
import com.gastocks.server.services.IQuoteService
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
@CompileStatic
class AVGlobalQuoteService implements IQuoteService {

    private static final String API_KEY = "W2OXJLZJ9W0O5K1M"
    private static final String API_KEY_PARAM = "&apikey="

    AVGlobalQuote getQuote(String symbol) {

        AVGlobalQuote quote = new AVGlobalQuote()

        HttpURLConnection conn

        def startStopwatch = System.currentTimeMillis()

        try {
            URL url = new URL("${AVGlobalQuoteConstants.AV_GLOBAL_QUOTE_URI}${symbol}${API_KEY_PARAM}${API_KEY}")
            conn = (HttpURLConnection) url.openConnection()
            conn.setRequestMethod("GET")
            conn.setRequestProperty("Accept", "application/json")

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code [${conn.getResponseCode()}]")
            }

            def slurped = new JsonSlurper().parse(url)

            log.info "Slurped data for symbol [${symbol}]: [${slurped}]"

            if (!AVGlobalQuoteConverter.hasData(slurped)) {
                log.warn("No quote data found for symbol [${symbol}]")
                return null
            } else {
                quote = AVGlobalQuoteConverter.fromAVGlobalQuote(slurped)
                log.info "AVGlobalQuote: [${quote}]"
            }

        } catch (Exception ex) {
            ex.printStackTrace()
            return null
        } finally {
            conn?.disconnect()
        }

        log.info "AVGlobalQuote retrieved in [${System.currentTimeMillis() - startStopwatch} ms]"
        quote
    }


}
