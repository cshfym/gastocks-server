package com.gastocks.server.schedulers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import java.text.SimpleDateFormat

@Component
class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class)

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss")

    @Scheduled(fixedRate = 5000L)
    void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()))
    }
}
