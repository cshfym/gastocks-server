package com.gastocks.server.services

import com.gastocks.server.converters.IConverter
import com.gastocks.server.models.IQuote

interface IQuoteService {

    abstract String getResourceUrlString()

    abstract <T extends IConverter> T getConverter()

    abstract <T extends IQuote> T getQuote(String symbol)

}