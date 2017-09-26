package com.gastocks.server.services.redis

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import redis.clients.jedis.Jedis

@Slf4j
@Service
class RedisConnectionService {

    @Value('${redis.connection.url}')
    String REDIS_CONNECTION_URL

    @Value('${redis.connection.port}')
    int REDIS_CONNECTION_PORT

    @Value('${redis.connection.password}')
    String REDIS_CONNECTION_PW

    private Jedis jedis

    void setCache(String key, String value) {

        if (!jedis) { initialize() }

        jedis.set(key, value)
    }

    String getFromCache(String key) {

        if (!jedis) { initialize() }

        jedis.get(key)
    }

    private void initialize() {
        jedis = new Jedis(REDIS_CONNECTION_URL, REDIS_CONNECTION_PORT)
        jedis.connect()
        jedis.auth(REDIS_CONNECTION_PW)

        log.info("Established connecion with Redis at ${REDIS_CONNECTION_URL}:${REDIS_CONNECTION_PORT}: [${jedis.ping()}]")
    }
}
