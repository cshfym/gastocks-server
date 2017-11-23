package com.gastocks.server.resources

import com.gastocks.server.models.domain.PersistableIndustry
import com.gastocks.server.services.Industry.IndustryApiService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/industries")
class IndustryResource {

    @Autowired
    IndustryApiService industryApiService

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    List<PersistableIndustry> findAllIndustries() {
        industryApiService.findAllIndustries()
    }

}