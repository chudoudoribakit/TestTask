package com.test_leon.integration;

import com.github.dockerjava.api.DockerClient;
import com.test_leon.integration.config.PostgresContainerInitializer;
import com.test_leon.entity.CurrentTime;
import com.test_leon.repository.CurrentTimeRepository;
import com.test_leon.service.BacklogFlusher;
import com.test_leon.service.CurrentTimeBacklog;
import com.test_leon.service.TimeScheduler;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.data.domain.Sort;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

@SpringBootTest(properties = "spring.datasource.hikari.connection-timeout=1000")
@Testcontainers
public class ConnectionLossRecoveryIT extends PostgresContainerInitializer {

    @MockBean
    private TimeScheduler timeScheduler;

    @Autowired
    private CurrentTimeBacklog backlog;

    @Autowired
    private BacklogFlusher flusher;

    @Autowired
    private CurrentTimeRepository repo;

    @Autowired
    private HikariDataSource dataSource;

    @BeforeEach
    void cleanState() {
        repo.deleteAll();
        backlog.takeAllInstants();
    }

    @Test
    void dbPause_thenResume_preservesOrder() {
        Instant base = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        List<Instant> sample = LongStream.range(0, 5)
                .mapToObj(base::plusMillis)
                .toList();
        sample.forEach(backlog::enqueueOrThrow);

        pausePostgres();
        dataSource.getHikariPoolMXBean().softEvictConnections();

        assertThatThrownBy(repo::count)
                .isInstanceOfAny(DataAccessException.class,
                        CannotCreateTransactionException.class);

        resumePostgres();
        flusher.flushAsync();

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(backlog::isEmpty);

        List<Instant> persisted = repo.findAll(Sort.by("recordedAt"))
                .stream()
                .map(CurrentTime::getRecordedAt)
                .toList();

        assertThat(persisted)
                .hasSize(sample.size())
                .containsExactlyElementsOf(sample);
    }

    private DockerClient docker() {
        return POSTGRES.getDockerClient();
    }

    private void pausePostgres() {
        docker().pauseContainerCmd(POSTGRES.getContainerId()).exec();
    }

    private void resumePostgres() {
        docker().unpauseContainerCmd(POSTGRES.getContainerId()).exec();
    }
}