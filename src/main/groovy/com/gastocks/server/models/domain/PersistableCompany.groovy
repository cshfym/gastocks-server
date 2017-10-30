package com.gastocks.server.models.domain

import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

/**
 * Company object
 */
@Entity(name="company")
class PersistableCompany {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    @ManyToOne
    @JoinColumn(name = "symbol_id")
    PersistableSymbol symbol

    @ManyToOne
    @JoinColumn(name = "industry_id")
    PersistableIndustry industry

    @ManyToOne
    @JoinColumn(name = "sector_id")
    PersistableSector sector

    @ManyToOne
    @JoinColumn(name="exchange_market_id")
    PersistableExchangeMarket exchangeMarket

    String name
    String legalEntityIdentifier // Legal Entity Identifier for the company
    String legalName
    int employeeCount

    String shortDescription
    String longDescription
    String ceo
    String companyUrl
    String businessAddress
    String mailingAddress
    String businessPhoneNumber
    String headquarterAddressLine1
    String headquarterAddressLine2
    String headquarterAddressCity
    String headquarterAddressPostalCode
    String headquarterState
    String incorporatedState
    String incorporatedCountry

    String entityLegalForm
    String cik // Central Index Key issued by the SEC, which is the unique identifier all company filings are issued under
    String sic
    Date latestFilingDate

    boolean entityStatus
    boolean standardizedActive

    String template

    String jsonDump
}

