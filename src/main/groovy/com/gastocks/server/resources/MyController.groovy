package com.gastocks.server.resources

import com.gastocks.server.services.AVGlobalQuoteFetchAndPersistService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MyController {

    @Autowired
    AVGlobalQuoteFetchAndPersistService quoteFetchService

    @RequestMapping(value = "/")
    def @ResponseBody hello() {
        "Hello World!"

        quoteFetchService.fetchAndPersistAllQuotes()
    }

}