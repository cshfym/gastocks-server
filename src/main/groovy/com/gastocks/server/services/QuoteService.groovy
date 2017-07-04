package com.gastocks.server.services

import com.gastocks.server.models.Quote
import groovy.transform.CompileStatic
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
@CompileStatic
class QuoteService {

    private static final String alphaVantageGlobalQuoteSymbolUri = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
    private static final String apiKey = "W2OXJLZJ9W0O5K1M"


    Quote getQuote(String symbol) {

        /**
         // https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=MSFT&apikey=demo
         Quote quote = restTemplate.getForObject(alphaVantageGlobalQuoteSymbolUri + symbol + apiKeyParam, Quote.class)
         **/

        try {

            URL url = new URL("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=${symbol}&apikey=${apiKey}")
            HttpURLConnection conn = (HttpURLConnection) url.openConnection()
            conn.setRequestMethod("GET")
            conn.setRequestProperty("Accept", "application/json")

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode())
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())))

            String output
            System.out.println("Output from Server .... \n")
            while ((output = br.readLine()) != null) {
                System.out.println(output)
            }

            conn.disconnect()

        } catch (MalformedURLException e) {

            e.printStackTrace()

        } catch (IOException e) {

            e.printStackTrace()

        }

        new Quote("XYZ", new Double(0.1), new Double(0.2), new Double(0.3))
    }

/*
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
	return builder.build()
  }
  */
}
