package com.test_leon.dto;

import java.time.Instant;

public record CurrentTimeDto(Long id, Instant recordedAt) {
}