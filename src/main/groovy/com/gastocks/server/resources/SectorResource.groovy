package com.gastocks.server.resources

import com.gastocks.server.models.BasicResponse
import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.schedulers.SectorQuoteBackfillScheduledTask
import com.gastocks.server.services.sector.SectorApiService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/sectors")
class SectorResource {

    @Autowired
    SectorApiService sectorApiService

    @Autowired
    SectorQuoteBackfillScheduledTask sectorQuoteBackfillScheduledTask

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    List<PersistableSector> findAllSectors() {
        sectorApiService.findAllSectors()
    }

    @ResponseBody
    @RequestMapping(value="/{identifier}", method=RequestMethod.GET)
    PersistableSector getSectorBySymbolIdentifier(@PathVariable("identifier") String identifier) {
        sectorApiService.findSectorBySymbolIdentifier(identifier)
    }

    @ResponseBody
    @RequestMapping(value="/backfill", method=RequestMethod.POST)
    BasicResponse backfill() {
        sectorQuoteBackfillScheduledTask.process()
        new BasicResponse(success: true)
    }

}