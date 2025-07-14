package com.kadir.smartirrigation.infrastructure.init;

import com.kadir.smartirrigation.domain.model.temperature.TemperatureConfig;
import com.kadir.smartirrigation.infrastructure.repository.temperature.TemperatureConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TemperatureConfigInitializer {
    private final TemperatureConfigRepository repository;

    @PostConstruct
    public void init() {
        if (repository.findById(1L).isEmpty()) {
            repository.save(TemperatureConfig.builder()
                    .id(1L)
                    .threshold(30.0)
                    .extraSeconds(10)
                    .active(true)
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
    }
}
