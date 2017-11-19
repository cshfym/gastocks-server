package com.gastocks.server.converters.company

import com.gastocks.server.models.domain.PersistableCompany
import com.gastocks.server.models.domain.PersistableCompanyDump
import com.gastocks.server.models.domain.PersistableExchangeMarket
import com.gastocks.server.models.domain.PersistableIndustry
import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.exchangemarket.ExchangeMarketConstants
import com.gastocks.server.services.domain.CompanyPersistenceService
import com.gastocks.server.services.domain.ExchangeMarketPersistenceService
import com.gastocks.server.services.domain.IndustryPersistenceService
import com.gastocks.server.services.domain.SectorPersistenceService
import com.gastocks.server.services.domain.SymbolPersistenceService
import com.gastocks.server.util.DateUtility
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Slf4j
@Component
class CompanyConverter {

    @Autowired
    SymbolPersistenceService symbolPersistenceService

    @Autowired
    ExchangeMarketPersistenceService exchangeMarketPersistenceService

    @Autowired
    IndustryPersistenceService industryPersistenceService

    @Autowired
    SectorPersistenceService sectorPersistenceService

    @Autowired
    CompanyPersistenceService companyPersistenceService

    final static String EMPTY_STRING = ""

    PersistableCompany fromJsonDump(PersistableCompanyDump companyDump) {

        def startStopwatch = System.currentTimeMillis()

        def jsonSlurper = new JsonSlurper()
        def companyData = jsonSlurper.parseText(companyDump.jsonDump)

        PersistableExchangeMarket exchangeMarket
        try {
            exchangeMarket = resolvePersistableExchangeMarket(companyData.stock_exchange)
        } catch (Exception ex) {
            exchangeMarket = exchangeMarketPersistenceService.findByShortName(ExchangeMarketConstants.NEW_YORK_STOCK_EXCHANGE)
        }

        PersistableSymbol persistableSymbol
        try {
            persistableSymbol = resolvePersistableSymbol(companyData.ticker, exchangeMarket, companyData.name)
        } catch (Exception ex) {
            log.warn("Could not resolve persistable symbol from ticker [${companyData.ticker}], aborting.")
            return
        }

        Date latestFilingDate
        try {
            latestFilingDate = DateUtility.parseDateString(companyData.latest_filing_date)
        } catch (Exception ex) {
            latestFilingDate = null
        }

        PersistableSector sector
        try {
            sector = resolvePersistableSector(companyData.sector)
        } catch (Exception ex) {
            log.warn("Error finding or persisting sector [${companyData.sector}] for symbol [${companyData.ticker}], aborting.")
            return
        }

        PersistableIndustry industry
        try {
            industry = resolvePersistableIndustry(companyData.industry_category, companyData.industry_group)
        } catch (Exception ex) {
            log.warn("Error finding or persisting industry [${companyData.industry_category} / ${companyData.industry_group}] " +
                    "for symbol [${companyData.ticker}], aborting.")
            return
        }

        String hqAddress = companyData.hq_address1 ?: EMPTY_STRING
        (companyData.hq_address2) ? hqAddress = (hqAddress + companyData.hq_address2.toString()) : hqAddress

        PersistableCompany company
        try {
            company = new PersistableCompany(
                symbol: persistableSymbol,
                exchangeMarket: exchangeMarket,
                sector: sector,
                industry: industry,
                latestFilingDate: latestFilingDate,
                name: companyData.name,
                legalEntityIdentifier: companyData.lei ?: EMPTY_STRING,
                legalName: companyData.legal_name ?: EMPTY_STRING,
                employeeCount: companyData.employees ?: EMPTY_STRING,
                shortDescription: companyData.short_description ?: EMPTY_STRING,
                longDescription: companyData.long_description ?: EMPTY_STRING,
                ceo: companyData.ceo ?: EMPTY_STRING,
                companyUrl: companyData.company_url ?: EMPTY_STRING,
                businessAddress: companyData.business_address ?: EMPTY_STRING,
                mailingAddress: companyData.mailing_address ?: EMPTY_STRING,
                businessPhoneNumber: companyData.business_phone_no ?: EMPTY_STRING,
                headquarterAddress: hqAddress,
                headquarterAddressCity: companyData.hq_address_city ?: EMPTY_STRING,
                headquarterAddressPostalCode: companyData.hq_address_postal_code ?: EMPTY_STRING,
                headquarterState: companyData.hq_state ?: EMPTY_STRING,
                incorporatedState: companyData.inc_state ?: EMPTY_STRING,
                incorporatedCountry: companyData.inc_country ?: EMPTY_STRING,
                entityLegalForm: companyData.entity_legal_form ?: EMPTY_STRING,
                cik: companyData.cik ?: EMPTY_STRING,
                sic: companyData.sic ?: EMPTY_STRING, // Safe parse int
                entityStatus: companyData.entity_status ?: false,
                standardizedActive: companyData.standardized_active ?: false,
                template: companyData.template ?: EMPTY_STRING,
                jsonDump: companyDump.jsonDump)

        } catch (Exception ex) {
            log.error("Exception: ", ex)
        }

        if (company) {
            try {
                companyPersistenceService.persistCompany(company)
                companyPersistenceService.deleteCompanyDump(companyDump)
            } catch (Exception ex) {
                log.error("Exception saving PersistableCompany: [${ex.message}]")
                return
            }
        }

        log.info("CompanyDump converted to PersistableCompany object in [${System.currentTimeMillis() - startStopwatch} ms]")
    }

    PersistableSymbol resolvePersistableSymbol(String identifier, PersistableExchangeMarket exchangeMarket, String name) {

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)
        if (!symbol) {
            try {
                symbol = symbolPersistenceService.persistSymbol(
                    new PersistableSymbol(
                        identifier: identifier,
                        description: name,
                        active: true,
                        exchangeMarket: exchangeMarket
                    )
                )
            } catch (Exception ex) {
                log.error("Could not persist new symbol with identifier [${identifier}]")
                throw ex
            }
        }

        symbol
    }

    PersistableExchangeMarket resolvePersistableExchangeMarket(String value) {

        value = ExchangeMarketConstants.resolveMarketName(value)

        PersistableExchangeMarket market = exchangeMarketPersistenceService.findByShortName(value)
        if (!market) {
            throw new IllegalArgumentException("Could not resolve PersistableExchangeMarket [${value}] in ${this.class} during object conversion.")
        }
        market
    }

    PersistableIndustry resolvePersistableIndustry(String category, String group) {

        if (!category || (category == "null")) {
            category = "n/a"
            group = EMPTY_STRING
        }

        PersistableIndustry industry = industryPersistenceService.findByCategoryAndSubCategory(category, group)

        if (!industry) {
            industry = industryPersistenceService.createPersistableIndustry(category, group)
        }

        industry
    }

    PersistableSector resolvePersistableSector(String description) {

        if (!description || (description == "null")) { description = "n/a" }

        PersistableSector sector = sectorPersistenceService.findByDescription(description)

        if (!sector) {
            sector = sectorPersistenceService.persistNewSector(new PersistableSector(description: description))
        }

        sector
    }

}
