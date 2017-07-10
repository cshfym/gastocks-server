package com.gastocks.server.converters

import com.gastocks.server.models.IQuote

interface IConverter {

    abstract boolean hasData(Object obj)

    abstract <T extends IQuote> T fromObject(Object obj)
}