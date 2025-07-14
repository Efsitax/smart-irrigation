package com.kadir.smartirrigation.domain.model.temperature;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "temperature_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemperatureConfig {
    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private Double threshold;

    @Column(name = "extra_seconds", nullable = false)
    private int extraSeconds;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
