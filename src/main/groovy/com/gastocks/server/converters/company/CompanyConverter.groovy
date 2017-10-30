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
import com.gastocks.server.util.NumberUtility
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


    PersistableCompany fromJson(String companyJson) {

        def jsonSlurper = new JsonSlurper()
        def companyData = jsonSlurper.parseText(companyJson)

        new PersistableCompany(
            symbol: resolvePersistableSymbol(companyData.ticker as String),
            exchangeMarket: resolvePersistableExchangeMarket(companyData.stock_exchange),
            sector: resolvePersistableSector(companyData.sector),
            industry: resolvePersistableIndustry(companyData.industry_category, companyData.industry_group),
            latestFilingDate: DateUtility.parseDateString(companyData.latest_filing_date as String),
            name: companyData.name,
            legalEntityIdentifier: companyData.lei,
            legalName: companyData.legal_name,
            employeeCount: NumberUtility.safeProcessInteger(companyData.employees as String),
            shortDescription: companyData.short_description,
            longDescription: companyData.long_description,
            ceo: companyData.ceo,
            companyUrl: companyData.company_url,
            businessAddress: companyData.business_address,
            mailingAddress: companyData.mailing_address,
            businessPhoneNumber: companyData.business_phone_no,
            headquarterAddressLine1: companyData.hq_address1,
            headquarterAddressLine2: companyData.hq_address2,
            headquarterAddressCity: companyData.hq_address_city,
            headquarterAddressPostalCode: companyData.hq_address_postal_code,
            headquarterState: companyData.hq_state,
            incorporatedState: companyData.inc_state,
            incorporatedCountry: companyData.inc_country,
            entityLegalForm: companyData.entity_legal_form,
            cik: companyData.cik,
            sic: NumberUtility.safeProcessInteger(companyData.sic as String), // Safe parse int
            entityStatus: companyData.entity_status,
            standardizedActive: companyData.standardized_active,
            template: companyData.template,
            jsonDump: companyJson
        )
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
