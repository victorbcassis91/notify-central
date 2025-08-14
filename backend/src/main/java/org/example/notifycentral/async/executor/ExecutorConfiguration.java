package org.example.notifycentral.async.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfiguration { // thread pool executor, vertical scalability

    @Value("${notifycentral.executor.core-pool-size:4}")
    protected int coreSize;
    @Value("${notifycentral.executor.max-pool-size:8}")
    protected int maxSize;
    @Value("${notifycentral.executor.queue-capacity:100}")
    protected int queueCapacity;

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("notify-");
        executor.initialize();

        return executor;
    }

}
