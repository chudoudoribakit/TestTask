package com.test_leon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "current_times")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "current_time_seq")
    @SequenceGenerator(
            name = "current_time_seq",
            sequenceName = "current_time_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "recorded_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant recordedAt;
}
