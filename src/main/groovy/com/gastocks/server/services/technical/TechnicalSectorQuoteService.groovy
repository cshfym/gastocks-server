package com.gastocks.server.services.technical

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.converters.quote.TechnicalQuoteConverter
import com.gastocks.server.models.domain.PersistableCompany
import com.gastocks.server.models.domain.PersistableQuote
import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.exception.QuoteNotFoundException
import com.gastocks.server.models.sector.TechnicalSectorQuote
import com.gastocks.server.models.technical.TechnicalDataWrapper
import com.gastocks.server.models.technical.request.TechnicalQuoteRequestParameters
import com.gastocks.server.models.technical.response.TechnicalQuote
import com.gastocks.server.models.technical.response.TechnicalQuoteMetadata
import com.gastocks.server.models.technical.response.TechnicalQuoteParameters
import com.gastocks.server.services.domain.QuotePersistenceService
import com.gastocks.server.services.domain.SectorPersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import com.gastocks.server.services.intrinio.company.CompanyService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

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


    @Cacheable(value = CacheConfiguration.GET_TECHNICAL_QUOTE_FOR_SECTOR)
    List<TechnicalSectorQuote> getTechnicalQuoteForSector(String sector, TechnicalQuoteRequestParameters parameters) {

        // TODO Drop quote dates older than a certain threshold depending on the sector.

        def startStopwatch = System.currentTimeMillis()

        PersistableSector persistableSector = sectorPersistenceService.findByDescription(sector)
        List<PersistableCompany> companiesInSector = companyService.findAllCompaniesBySector(persistableSector)
        List<PersistableSymbol> symbolsInSector = companiesInSector.collect { it.symbol }

        Map<Date,TechnicalSectorQuote> sectorQuoteMap = [:]
        Map<Date,Integer> sectorQuoteCountMap = [:]

        List<PersistableQuote> allQuotesForSymbolsInSector = quotePersistenceService.findAllQuotesForSymbolsIn(symbolsInSector)
        addQuotesToSectorQuoteMap(sectorQuoteMap, sectorQuoteCountMap, allQuotesForSymbolsInSector, sector)

        /*
        symbolsInSector.each { persistableSymbol ->
            List<PersistableQuote> quotesForSymbol = quotePersistenceService.findAllQuotesForSymbol(persistableSymbol)
            addQuotesToSectorQuoteMap(sectorQuoteMap, sectorQuoteCountMap, quotesForSymbol, sector)
        }
        */

        log.info("Loaded [${sectorQuoteMap?.size()}] quotes for sector [${sector}] in [${System.currentTimeMillis() - startStopwatch} ms]")

        List<TechnicalSectorQuote> sectorQuoteList = []

        // Calculate averages based on the count of quotes in each date.
        sectorQuoteMap.each { key, technicalSectorQuote ->
            int quoteCountForDate = sectorQuoteCountMap.getOrDefault(key, 0)
            if (technicalSectorQuote) {
                technicalSectorQuote.averagePrice = ((double)(technicalSectorQuote.averagePrice / quoteCountForDate)).round(4)
                technicalSectorQuote.averageOpen = ((double)(technicalSectorQuote.averageOpen / quoteCountForDate)).round(4)
                technicalSectorQuote.averageHigh = ((double)(technicalSectorQuote.averageHigh / quoteCountForDate)).round(4)
                technicalSectorQuote.averageLow = ((double)(technicalSectorQuote.averageLow / quoteCountForDate)).round(4)
                technicalSectorQuote.averageVolume = ((double)(technicalSectorQuote.averageVolume / quoteCountForDate)).round(4)
                sectorQuoteList << technicalSectorQuote
            }
        }
        sectorQuoteList.sort { q1, q2 -> q1.quoteDate <=> q2.quoteDate }
    }

    protected static void addQuotesToSectorQuoteMap(Map<Date,TechnicalSectorQuote> sectorQuoteMap, Map<Date,Integer> sectorQuoteCountMap,
        List<PersistableQuote> quotes, String sector) {

        quotes.each { quote ->

            TechnicalSectorQuote technicalSectorQuote

            if (sectorQuoteMap.get(quote.quoteDate)) {
                technicalSectorQuote = sectorQuoteMap.get(quote.quoteDate)
                technicalSectorQuote.averagePrice += quote.price
                technicalSectorQuote.averageOpen += quote.dayOpen
                technicalSectorQuote.averageHigh += quote.dayHigh
                technicalSectorQuote.averageLow += quote.dayLow
                technicalSectorQuote.averageVolume += quote.volume
            } else {
                technicalSectorQuote = new TechnicalSectorQuote(
                    quoteDate: quote.quoteDate,
                    sector: sector,
                    averagePrice: quote.price,
                    averageOpen: quote.dayOpen,
                    averageHigh: quote.dayHigh,
                    averageLow: quote.dayLow,
                    averageVolume: quote.volume
               )
            }

            sectorQuoteMap.put(quote.quoteDate, technicalSectorQuote)

            int countQuotesForDate = sectorQuoteCountMap.getOrDefault(quote.quoteDate, 0)
            sectorQuoteCountMap.put(quote.quoteDate, (countQuotesForDate + 1))
        }
    }

}
