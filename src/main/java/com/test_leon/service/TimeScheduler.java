package com.test_leon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class TimeScheduler {

    private final CurrentTimeBacklog backlog;
    private final BacklogFlusher flusher;

    @Scheduled(
            fixedRateString = "${scheduler.tick-rate-ms}",
            scheduler       = "virtualSingleThreadScheduler"
    )
    public void tick() {
        backlog.enqueueOrThrow(Instant.now());
        flusher.flushAsync();
    }
}