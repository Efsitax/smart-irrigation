package com.kadir.smartirrigation.motor.service.impl;

import com.kadir.smartirrigation.motor.dto.MotorStateDto;
import com.kadir.smartirrigation.motor.model.MotorState;
import com.kadir.smartirrigation.motor.model.MotorStatus;
import com.kadir.smartirrigation.motor.repository.MotorStateRepository;
import com.kadir.smartirrigation.motor.service.MotorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MotorServiceImpl implements MotorService {
    private final MotorStateRepository repository;

    @Override
    public void updateStatus(MotorStateDto dto) {
        MotorState state = handleDefaultMotorState();

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            MotorStatus status = parseMotorStatus(dto.getStatus());
            if (status != null) {
                state.setStatus(status);
            } else {
                System.err.println("Skipping update due to invalid status: " + dto.getStatus());
            }
        }

        state.setUpdatedAt(LocalDateTime.now());

        repository.save(state);
    }

    @Override
    public void updateAutoControl(boolean enabled) {
        MotorState state = handleDefaultMotorState();

        state.setAutoControlEnabled(enabled);
        state.setUpdatedAt(LocalDateTime.now());
        repository.save(state);
    }

    @Override
    public MotorStateDto getStatus() {
        MotorState state = handleDefaultMotorState();

        MotorStateDto dto = new MotorStateDto();
        dto.setStatus(state.getStatus().name());
        dto.setAutoControlEnabled(state.isAutoControlEnabled());
        return dto;
    }

    private MotorState handleDefaultMotorState() {
        return repository.findById(1L).orElseGet(() -> {
            MotorState newState = new MotorState();
            newState.setId(1L);
            newState.setStatus(MotorStatus.OFF);
            newState.setAutoControlEnabled(true);
            newState.setUpdatedAt(LocalDateTime.now());
            return newState;
        });
    }

    private MotorStatus parseMotorStatus(String status) {
        try {
            return MotorStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
