package com.test_leon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean("persistenceExecutor")
    public Executor persistenceExecutor() {
        return new VirtualThreadTaskExecutor("db-flusher-");
    }
}