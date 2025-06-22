package com.test_leon.service;

import com.test_leon.exception.BacklogOverflowException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@ExtendWith(MockitoExtension.class)
public class CurrentTimeBacklogTest {

    private CurrentTimeBacklog backlog;

    @BeforeEach
    void init() {
        backlog = new CurrentTimeBacklog();
        ReflectionTestUtils.setField(backlog, "maxCapacity", 2);
    }

    @Test
    void enqueueOrThrow_overLimit_throwsException() {
        backlog.enqueueOrThrow(Instant.now());
        backlog.enqueueOrThrow(Instant.now());

        assertThatThrownBy(() -> backlog.enqueueOrThrow(Instant.now()))
                .isInstanceOf(BacklogOverflowException.class);
    }

    @Test
    void drain_returnsAllAndEmptiesQueue() {
        Instant t1 = Instant.now();
        Instant t2 = t1.plusSeconds(1);

        backlog.enqueueOrThrow(t1);
        backlog.enqueueOrThrow(t2);

        List<Instant> drained = backlog.takeAllInstants();

        assertThat(drained).containsExactlyInAnyOrder(t1, t2);
        assertThat(backlog.isEmpty()).isTrue();
    }
}
