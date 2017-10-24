package com.gastocks.server.resources

import com.gastocks.server.models.BasicResponse
import com.gastocks.server.services.intrinio.company.CompanyApiService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/intrinio/company")
class IntrinioCompanyResource {

    @Autowired
    CompanyApiService companyApiService

    /**
     * Convenience API to trigger load of company data from Intrinio
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/backfill", method=RequestMethod.POST)
    BasicResponse backfill() {
        companyApiService.backfill()
        new BasicResponse(success: true)
    }

}