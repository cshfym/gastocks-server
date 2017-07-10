package com.gastocks.server.resources

import com.gastocks.server.services.avglobalquote.AVGlobalQuoteFetchAndPersistService
import com.gastocks.server.services.avtimeseriesadjusted.AVTimeSeriesAdjustedFetchAndPersistService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MyController {

    @Autowired
    //AVGlobalQuoteFetchAndPersistService service
    AVTimeSeriesAdjustedFetchAndPersistService service

    @RequestMapping(value = "/")
    def @ResponseBody hello() {
        "Hello World!"

        service.fetchAndPersistAllQuotes()
    }

}