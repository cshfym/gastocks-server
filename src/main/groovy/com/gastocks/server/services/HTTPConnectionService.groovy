package com.gastocks.server.services

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Generic, all-purpose HTTP connection service.
 */
@Slf4j
@Service
class HTTPConnectionService {

    final static String  EMPTY_STRING = ""

    String getData(String address, RequestMethod method, String base64EncodedAuthorization) {

        def response = null

        def startStopwatch = System.currentTimeMillis()

        HttpURLConnection conn = null

        try {
            URL url = new URL(address)
            conn = (HttpURLConnection) url.openConnection()
            conn.setRequestMethod(method.toString())
            conn.setRequestProperty("Accept", "application/json")

            if (base64EncodedAuthorization) {
                conn.setRequestProperty("Authorization", "Basic " + base64EncodedAuthorization)
            }

            conn.connect()

            def responseCode = conn.responseCode

            switch (responseCode) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.inputStream))
                    StringBuilder sb = new StringBuilder()
                    String line
                    while ((line = br.readLine()) != null) { sb.append(line) }
                    br.close()
                    response = sb.toString()
                    break
                case 401:
                    log.warn("401 Unauthorized response from [${url.text}]")
                    return EMPTY_STRING
                    break
                case 404:
                    log.warn("404 Not Found at address [${url.text}]")
                    return EMPTY_STRING
                    break
                case 429:
                    log.warn("429 Access Denied - too many requests at [${url.text}]")
                    return EMPTY_STRING
                    break
                default:
                    throw new RuntimeException("Failed : HTTP error code [${responseCode}]")
            }
            log.trace "Slurped data at [${url.path}]: [${response}]"
        } catch (Exception ex) {
            log.error("Exception caught handling uri [${address}]: [${ex.message}]")
            return EMPTY_STRING
        } finally {
            conn?.disconnect()
        }

        log.info "Retrieved data from [${address}] in [${System.currentTimeMillis() - startStopwatch} ms]"

        response
    }
}
