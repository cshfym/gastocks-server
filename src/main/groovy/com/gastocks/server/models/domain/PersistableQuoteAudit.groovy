package com.gastocks.server.models.domain

import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@ToString
@Entity (name="quote_audit")
class PersistableQuoteAudit {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    String id

    @ManyToOne
    @JoinColumn(name = "symbol_id")
    PersistableSymbol symbol

    @ManyToOne
    @JoinColumn(name = "quote_id")
    PersistableQuote quote

    String auditText

}
