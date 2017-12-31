package com.gastocks.server.services.technical

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.models.domain.PersistableCompany
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.models.domain.PersistableSectorPerformance
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SectorPerformancePersistenceService
import com.gastocks.server.services.domain.SectorPersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import com.gastocks.server.services.intrinio.company.CompanyService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Slf4j
@Service
class TechnicalSectorQuoteService {

    @Autowired
    QuotePersistenceService quotePersistenceService

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    CompanyService companyService

    @Autowired
    SectorPersistenceService sectorPersistenceService

    @Autowired
    SectorPerformancePersistenceService sectorPerformancePersistenceService


    @Cacheable(value = CacheConfiguration.GET_TECHNICAL_QUOTE_FOR_SECTOR)
    List<PersistableSectorPerformance> getTechnicalQuoteForSector(String sector) {

        PersistableSector persistableSector = sectorPersistenceService.findByDescription(sector)
        List<PersistableSectorPerformance> sectorQuotes = sectorPerformancePersistenceService.findAllBySector(persistableSector)
        sectorQuotes.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate }
    }

    @Transactional
    void calculateAndPersistSectorPerformanceByQuoteDate(String sector, Date quoteDate) {

        def startStopwatch = System.currentTimeMillis()

        PersistableSector persistableSector = sectorPersistenceService.findByDescription(sector)
        List<PersistableCompany> companiesInSector = companyService.findAllCompaniesBySector(persistableSector)
        List<PersistableSymbol> symbolsInSector = companiesInSector.collect { it.symbol }

        Map<Date,PersistableSectorPerformance> sectorQuoteMap = [:]
        Map<Date,Integer> sectorQuoteCountMap = [:]

        List<PersistableQuote> allQuotesForSymbolsInSector = quotePersistenceService.findAllQuotesForSymbolsByDateIn(quoteDate, symbolsInSector)
        log.info("Found [${allQuotesForSymbolsInSector.size()}] quotes for sector [${sector}] on quote date [${quoteDate.toString()}]")

        addQuotesToSectorQuoteMap(sectorQuoteMap, sectorQuoteCountMap, allQuotesForSymbolsInSector, persistableSector)

        List<PersistableSectorPerformance> sectorPerformanceList = []

        // Calculate averages based on the count of quotes in each date.
        sectorQuoteMap.each { key, sectorPerformance ->
            int quoteCountForDate = sectorQuoteCountMap.getOrDefault(key, 0)
            if (sectorPerformance) {
                sectorPerformance.price = ((double)(sectorPerformance.price / quoteCountForDate)).round(3)
                sectorPerformance.dayOpen = ((double)(sectorPerformance.dayOpen / quoteCountForDate)).round(3)
                sectorPerformance.dayHigh = ((double)(sectorPerformance.dayHigh / quoteCountForDate)).round(3)
                sectorPerformance.dayLow = ((double)(sectorPerformance.dayLow / quoteCountForDate)).round(3)
                sectorPerformance.volume = ((double)(sectorPerformance.volume / quoteCountForDate)).round(3)
                sectorPerformanceList << sectorPerformance
            }
        }

        sectorPerformanceList.each { sectorPerformance ->
            sectorPerformancePersistenceService.persistSectorPerformance(sectorPerformance)
        }

        sectorQuoteMap.clear()
        companiesInSector.clear()
        symbolsInSector.clear()
        allQuotesForSymbolsInSector.clear()

        log.info("Finished processing sector quote [${sector}] for quote date [${quoteDate.toString()}] in [${System.currentTimeMillis() - startStopwatch} ms]")
    }

    protected static void addQuotesToSectorQuoteMap(Map<Date,PersistableSectorPerformance> sectorQuoteMap, Map<Date,Integer> sectorQuoteCountMap,
        List<PersistableQuote> quotes, PersistableSector sector) {

        quotes.each { quote ->

            PersistableSectorPerformance persistableSectorPerformance

            if (sectorQuoteMap.get(quote.quoteDate)) {
                persistableSectorPerformance = sectorQuoteMap.get(quote.quoteDate)
                persistableSectorPerformance.price += quote.price
                persistableSectorPerformance.dayOpen += quote.dayOpen
                persistableSectorPerformance.dayHigh += quote.dayHigh
                persistableSectorPerformance.dayLow += quote.dayLow
                persistableSectorPerformance.volume += quote.volume
            } else {
                persistableSectorPerformance = new PersistableSectorPerformance(
                    quoteDate: quote.quoteDate,
                    sector: sector,
                    price: quote.price,
                    dayOpen: quote.dayOpen,
                    dayHigh: quote.dayHigh,
                    dayLow: quote.dayLow,
                    volume: quote.volume
               )
            }

            sectorQuoteMap.put(quote.quoteDate, persistableSectorPerformance)

            int countQuotesForDate = sectorQuoteCountMap.getOrDefault(quote.quoteDate, 0)
            sectorQuoteCountMap.put(quote.quoteDate, (countQuotesForDate + 1))
        }
    }

}
