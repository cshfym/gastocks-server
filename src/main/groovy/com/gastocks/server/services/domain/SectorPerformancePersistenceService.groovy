package com.gastocks.server.services.domain

import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.models.domain.PersistableSectorPerformance
import com.gastocks.server.repositories.SectorPerformanceRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Service class for dealing with persistence-object-based requests.
 */
@Slf4j
@Service
class SectorPerformancePersistenceService {

    @Autowired
    SectorPerformanceRepository sectorPerformanceRepository

    List<PersistableSectorPerformance> findAllBySector(PersistableSector sector) {
        sectorPerformanceRepository.findAllBySector(sector)
    }

    PersistableSectorPerformance persistSectorPerformance(PersistableSectorPerformance sectorPerformance) {

        PersistableSectorPerformance existingSectorPerformance = sectorPerformanceRepository.findByQuoteDateAndSector(sectorPerformance.quoteDate, sectorPerformance.sector)

        if (!existingSectorPerformance) {
            // Create
            sectorPerformanceRepository.save(sectorPerformance)
            log.info("CREATED PersistableSectorPerformance for sector [${sectorPerformance.sector.description}] and quote date [${sectorPerformance.quoteDate}]")
            sectorPerformance
        } else {
            // Update if necessary
            if (sectorPerformanceRecordsAreIdentical(existingSectorPerformance, sectorPerformance)) {
                log.info("Bypassing update to sector [${sectorPerformance.sector.description}] and quote date [${sectorPerformance.quoteDate.toString()}], " +
                        "calculated sector quote is identical to previously persisted value.")
                return existingSectorPerformance
            }
            existingSectorPerformance.price = sectorPerformance.price
            existingSectorPerformance.dayOpen = sectorPerformance.dayOpen
            existingSectorPerformance.dayHigh = sectorPerformance.dayHigh
            existingSectorPerformance.dayLow = sectorPerformance.dayLow
            existingSectorPerformance.volume = sectorPerformance.volume
            sectorPerformanceRepository.save(existingSectorPerformance)
            log.info("UPDATED PersistableSectorPerformance for sector [${sectorPerformance.sector.description}] and quote date [${sectorPerformance.quoteDate}]")
            existingSectorPerformance
        }
    }

    protected boolean sectorPerformanceRecordsAreIdentical(PersistableSectorPerformance existingSectorPerformance, PersistableSectorPerformance newSectorPerformance) {
        existingSectorPerformance.quoteDate == newSectorPerformance.quoteDate &&
        existingSectorPerformance.price == newSectorPerformance.price &&
        existingSectorPerformance.dayOpen == newSectorPerformance.dayOpen &&
        existingSectorPerformance.dayLow == newSectorPerformance.dayLow &&
        existingSectorPerformance.dayHigh == newSectorPerformance.dayHigh &&
        existingSectorPerformance.volume == newSectorPerformance.volume
    }

}
