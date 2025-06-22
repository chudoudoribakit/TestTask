package com.test_leon.service;

import com.test_leon.entity.CurrentTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class BacklogFlusher {

    private final CurrentTimeService currentTimeService;
    private final CurrentTimeBacklog backlog;

    private final ReentrantLock lock = new ReentrantLock();

    @Async("persistenceExecutor")
    @Retryable(
            retryFor = DataAccessException.class,
            backoff  = @Backoff(delay = 5_000)
    )
    public void flushAsync() {
        if (!lock.tryLock()) {
            return;
        }

        List<Instant> batch = Collections.emptyList();
        try {
            if (backlog.isEmpty()) {
                return;
            }

            batch = backlog.takeAllInstants();
            List<CurrentTime> entities = batch.stream()
                    .map(time -> CurrentTime.builder().recordedAt(time).build())
                    .toList();

            currentTimeService.saveAllTimes(entities);
            log.info("Flushed {} timestamps", batch.size());

        } catch (DataAccessException ex) {
            if (!batch.isEmpty()) {
                backlog.enqueueAll(batch);
            }
            log.warn("DB unreachable, will retry: {}", ex.getMessage());
            throw ex;
        } finally {
            lock.unlock();
        }
    }
}
