package com.test_leon.controller;

import com.test_leon.dto.CurrentTimeDto;
import com.test_leon.service.CurrentTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/times")
@RequiredArgsConstructor
public class CurrentTimeController {

    private final CurrentTimeService currentTimeService;

    @GetMapping()
    public List<CurrentTimeDto> getAllTimes() {
        return currentTimeService.findAll();
    }
}
