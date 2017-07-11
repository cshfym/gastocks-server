package com.gastocks.server.services.avtimeseriesadjusted

import com.gastocks.server.converters.IConverter
import com.gastocks.server.converters.avtimeseriesadjustedquote.AVTimeSeriesAdjustedQuoteConverter
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedQuoteConstants
import com.gastocks.server.services.QuoteService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
@CompileStatic
class AVTimeSeriesAdjustedQuoteService extends QuoteService {

    @Autowired
    AVTimeSeriesAdjustedQuoteConverter converter

    @Override
    String getResourceUrlString() {
        "${AVTimeSeriesAdjustedQuoteConstants.AV_TS_ADJ_QUOTE_FULL_URI}${API_KEY_PARAM}${API_KEY}${OUTPUT_SIZE_PARAM}"
    }

    @Override
    IConverter getConverter() {
        converter
    }

}
