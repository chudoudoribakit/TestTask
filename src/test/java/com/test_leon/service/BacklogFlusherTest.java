package com.test_leon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BacklogFlusherTest {

    @Mock
    private CurrentTimeService service;
    private CurrentTimeBacklog backlog;
    @InjectMocks
    private BacklogFlusher flusher;

    @BeforeEach
    void setup() {
        backlog = spy(new CurrentTimeBacklog());
        ReflectionTestUtils.setField(backlog, "maxCapacity", 10);
        flusher = new BacklogFlusher(service, backlog);
    }

    @Test
    void flushAsync_success_drainsQueueAndPersists() {
        backlog.enqueueOrThrow(Instant.parse("2025-06-22T10:00:00Z"));

        flusher.flushAsync();

        verify(service, times(1))
                .saveAllTimes(argThat(list -> list.size() == 1
                        && list.getFirst() != null));
        assertThat(backlog.isEmpty()).isTrue();
    }

    @Test
    void flushAsync_dbFailure_returnsBatchToQueue() {
        backlog.enqueueOrThrow(Instant.parse("2025-06-22T10:00:00Z"));

        doThrow(new DataAccessResourceFailureException("boom"))
                .when(service).saveAllTimes(anyList());

        assertThatThrownBy(flusher::flushAsync)
                .isInstanceOf(DataAccessResourceFailureException.class);

        assertThat(backlog.size()).isEqualTo(1);
        verify(service).saveAllTimes(anyList());
    }
}