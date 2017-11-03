package com.gastocks.server.converters.company

import com.gastocks.server.models.domain.PersistableCompany
import com.gastocks.server.models.domain.PersistableExchangeMarket
import com.gastocks.server.models.domain.PersistableIndustry
import com.gastocks.server.models.domain.PersistableSector
import com.gastocks.server.models.domain.PersistableSymbol
import com.gastocks.server.models.exchangemarket.ExchangeMarketConstants
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

    final static String EMPTY_STRING = ""

    PersistableCompany fromJson(String companyJson) {

        def jsonSlurper = new JsonSlurper()
        def companyData = jsonSlurper.parseText(companyJson)

        PersistableSymbol persistableSymbol
        try {
            persistableSymbol = resolvePersistableSymbol(companyData.ticker)
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

        PersistableExchangeMarket exchangeMarket
        try {
            exchangeMarket = resolvePersistableExchangeMarket(companyData.stock_exchange)
        } catch (Exception ex) {
            exchangeMarket = exchangeMarketPersistenceService.findByShortName(ExchangeMarketConstants.NEW_YORK_STOCK_EXCHANGE)
        }

        PersistableSector sector
        try {
            sector = resolvePersistableSector(companyData.sector)
        } catch (Exception ex) {
            sector = sectorPersistenceService.findByDescription("n/a")
        }

        PersistableIndustry industry
        try {
            resolvePersistableIndustry(companyData.industry_category, companyData.industry_group)
        } catch (Exception ex) {
            industry = industryPersistenceService.findByDescription("n/a")
        }

        try {
            new PersistableCompany(
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
                headquarterAddressLine1: companyData.hq_address1 ?: EMPTY_STRING,
                headquarterAddressLine2: companyData.hq_address2 ?: EMPTY_STRING,
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
                jsonDump: companyJson)

        } catch (Exception ex) {
            log.error("Exception: ", ex)
        }

    }

    PersistableSymbol resolvePersistableSymbol(String identifier) {

        PersistableSymbol symbol = symbolPersistenceService.findByIdentifier(identifier)
        if (!symbol) {
            throw new IllegalArgumentException("Could not resolve PersistableSymbol [${identifier}] in ${this.class} during object conversion.")
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
        log.info("Found category: [${category}], group: [${group}]")
    }

    PersistableSector resolvePersistableSector(String description) {

        if (!description || (description == "null")) { description = "n/a" }

        PersistableSector sector = sectorPersistenceService.findByDescription(description)
        if (!sector) {
            throw new IllegalArgumentException("Could not resolve PersistableSector [${description}] in ${this.class} during object conversion.")
        }
        sector
    }

}
