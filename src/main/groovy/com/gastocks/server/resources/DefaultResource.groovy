package com.gastocks.server.resources

import com.gastocks.server.models.BasicQuoteResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/")
class DefaultResource {

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    BasicQuoteResponse hello() {
        new BasicQuoteResponse(success: true, message: "Default resource. This URI does nothing.")
    }

}