package com.gastocks.server.services.avtimeseriesadjusted

import com.gastocks.server.converters.IConverter
import com.gastocks.server.converters.avtimeseriesadjustedquote.AVTimeSeriesAdjustedQuoteConverter
import com.gastocks.server.models.avtimeseriesadjusted.AVTimeSeriesAdjustedQuoteConstants
import com.gastocks.server.services.AbstractExternalQuoteService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@Slf4j
@CompileStatic
class AVTimeSeriesAdjustedQuoteService extends AbstractExternalQuoteService {

    @Value('${alphavantage.apikey}')
    String API_KEY

    @Autowired
    AVTimeSeriesAdjustedQuoteConverter converter

    @Override
    String getResourceUrlString() {
        // "${AVTimeSeriesAdjustedQuoteConstants.AV_TS_ADJ_QUOTE_FULL_URI}${API_KEY_PARAM}${API_KEY}${OUTPUT_SIZE_PARAM}"
        "${AVTimeSeriesAdjustedQuoteConstants.AV_TS_ADJ_QUOTE_FULL_URI}${API_KEY_PARAM}${API_KEY}" // Compact
    }

    @Override
    IConverter getConverter() {
        converter
    }

}
