package com.gastocks.server.services.intrinio

import org.springframework.beans.factory.annotation.Value

class IntrinioBaseService {

    @Value('${intrinio.base.url}')
    String INTRINIO_BASE_URL

    @Value('${intrinio.api.key}')
    String INTRINIO_API_KEY

    @Value('${intrinio.api.password}')
    String INTRINIO_API_PASSWORD

    String INTRINIO_COMPANY_PATH = "/companies"
    String INTRINIO_EXCHANGE_PRICE_PATH = "/prices/exchange"

}
