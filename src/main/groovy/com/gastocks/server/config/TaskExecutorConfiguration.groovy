package com.gastocks.server.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

@Component
class TaskExecutorConfiguration {

    @Value('${thread.pool.core.pool.size}')
    int THREAD_POOL_CORE_POOL_SIZE

    @Value('${thread.pool.max.pool.size}')
    int THREAD_POOL_MAX_POOL_SIZE

    @Value('${thread.pool.queue.capacity}')
    int THREAD_POOL_QUEUE_CAPACITY

    @Bean
    ThreadPoolTaskExecutor taskExecutor() {

        def threadPoolExecutor = new ThreadPoolTaskExecutor()

        threadPoolExecutor.setCorePoolSize(THREAD_POOL_CORE_POOL_SIZE)
        threadPoolExecutor.setMaxPoolSize(THREAD_POOL_MAX_POOL_SIZE)
        threadPoolExecutor.setQueueCapacity(THREAD_POOL_QUEUE_CAPACITY)

        threadPoolExecutor.setWaitForTasksToCompleteOnShutdown(true)

        threadPoolExecutor
    }
}
