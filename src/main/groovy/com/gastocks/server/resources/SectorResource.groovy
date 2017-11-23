package com.gastocks.server.resources

import com.gastocks.server.models.domain.PersistableSector
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

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    List<PersistableSector> findAllSectors() {
        sectorApiService.findAllSectors()
    }

}