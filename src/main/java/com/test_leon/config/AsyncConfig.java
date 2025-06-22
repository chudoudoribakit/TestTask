package com.test_leon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AsyncConfig {

    @Bean("persistenceExecutor")
    public Executor persistenceExecutor() {
        return new VirtualThreadTaskExecutor("db-flusher-");
    }

    @Bean("virtualSingleThreadScheduler")
    public TaskScheduler virtualSingleThreadScheduler() {
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(
                1,
                Thread.ofVirtual()
                        .name("virtual-scheduler-")
                        .factory()
        );
        return new ConcurrentTaskScheduler(scheduledExecutor);
    }
}