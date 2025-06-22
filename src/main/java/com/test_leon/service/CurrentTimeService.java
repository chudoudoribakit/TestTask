package com.test_leon.service;

import com.test_leon.dto.CurrentTimeDto;
import com.test_leon.entity.CurrentTime;
import com.test_leon.mapper.CurrentTimeMapper;
import com.test_leon.repository.CurrentTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentTimeService {

    private final CurrentTimeRepository repository;
    private final CurrentTimeMapper mapper;

    @Transactional
    public void saveAllTimes(List<CurrentTime> timeList) {
        repository.saveAll(timeList);
    }

    @Transactional(readOnly = true)
    public List<CurrentTimeDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }
}