package com.gastocks.server.services.avglobalquote

import com.gastocks.server.converters.IConverter
import com.gastocks.server.converters.avglobalquote.AVGlobalQuoteConverter
import com.gastocks.server.models.avglobalquote.AVGlobalQuoteConstants
import com.gastocks.server.services.AbstractExternalQuoteService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@Slf4j
@CompileStatic
class AVGlobalQuoteService extends AbstractExternalQuoteService {

    @Autowired
    AVGlobalQuoteConverter converter

    @Value('${alphavantage.apikey}')
    String API_KEY

    @Override
    String getResourceUrlString() {
        "${AVGlobalQuoteConstants.AV_GLOBAL_QUOTE_URI}${API_KEY_PARAM}${API_KEY}"
    }

    @Override
    IConverter getConverter() {
        converter
    }

}
