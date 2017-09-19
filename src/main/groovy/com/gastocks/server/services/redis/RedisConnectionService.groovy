package com.gastocks.server.services.redis

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import redis.clients.jedis.Jedis

@Slf4j
@Service
class RedisConnectionService {

    final static String REDIS_CONNECTION_URL = "104.236.144.134"
    final static int REDIS_CONNECTION_PORT = 7480
    final static String REDIS_CONNECTION_PW = "jediknight"


    Jedis jedis

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

        log.info("Establish connecion with Redis at ${REDIS_CONNECTION_URL}:${REDIS_CONNECTION_PORT}: [${jedis.ping()}]")
    }
}
