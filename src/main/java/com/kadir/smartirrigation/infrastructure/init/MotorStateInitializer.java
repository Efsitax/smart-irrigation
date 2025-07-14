package com.kadir.smartirrigation.infrastructure.init;

import com.kadir.smartirrigation.domain.model.motor.MotorState;
import com.kadir.smartirrigation.infrastructure.repository.motor.MotorStateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MotorStateInitializer {
    private final MotorStateRepository repository;

    @PostConstruct
    public void init() {
        if (repository.findById(1L).isEmpty()) {
            repository.save(MotorState.builder()
                    .id(1L)
                    .autoControl(false)
                    .moistureThreshold(40.0f)
                    .autoDurationSeconds(10)
                    .manualDurationSeconds(5)
                    .isOn(false)
                    .build());
        }
    }
}

