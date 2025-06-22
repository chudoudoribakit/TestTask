package com.test_leon.service;

import com.test_leon.dto.CurrentTimeDto;
import com.test_leon.entity.CurrentTime;
import com.test_leon.mapper.CurrentTimeMapper;
import com.test_leon.repository.CurrentTimeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrentTimeServiceTest {

    @Mock
    private CurrentTimeRepository repository;
    @Mock
    private CurrentTimeMapper mapper;

    @InjectMocks
    private CurrentTimeService service;

    @Test
    void saveAllTimes_delegatesToRepository() {
        List<CurrentTime> batch = List.of(
                CurrentTime.builder().recordedAt(Instant.now()).build()
        );

        service.saveAllTimes(batch);

        verify(repository).saveAll(batch);
    }

    @Test
    void findAll_returnsMappedDtos() {
        List<CurrentTime> entities = List.of(
                CurrentTime.builder().id(1L).recordedAt(Instant.now()).build()
        );
        List<CurrentTimeDto> dtos = List.of(new CurrentTimeDto(1L, Instant.now()));

        when(repository.findAll()).thenReturn(entities);
        when(mapper.toDtoList(entities)).thenReturn(dtos);

        List<CurrentTimeDto> result = service.findAll();

        assertThat(result).isEqualTo(dtos);
        verify(repository).findAll();
        verify(mapper).toDtoList(entities);
    }
}