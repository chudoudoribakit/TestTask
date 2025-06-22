package com.test_leon.service;

import com.test_leon.exception.BacklogOverflowException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class CurrentTimeBacklog {

    @Value("${test_leon.max-capacity}")
    private int maxCapacity;
    private final Queue<Instant> queue = new ConcurrentLinkedQueue<>();

    public void enqueueOrThrow(Instant time) {
        int currentSize = queue.size();
        if (currentSize >= maxCapacity) {
            throw new BacklogOverflowException(
                    "Backlog capacity " + maxCapacity + " reached; current size=" + currentSize
            );
        }
        queue.add(time);
    }

    public void enqueueAll(Collection<Instant> times) {
        queue.addAll(times);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public List<Instant> takeAllInstants() {
        List<Instant> batch = new ArrayList<>(queue.size());

        Instant time = queue.poll();

        while (time != null) {
            batch.add(time);
            time = queue.poll();
        }
        return batch;
    }
}
