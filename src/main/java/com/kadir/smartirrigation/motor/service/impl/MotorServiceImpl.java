package com.kadir.smartirrigation.motor.service.impl;

import com.kadir.smartirrigation.motor.dto.DurationDto;
import com.kadir.smartirrigation.motor.dto.MotorStateDto;
import com.kadir.smartirrigation.motor.dto.OnOffDto;
import com.kadir.smartirrigation.common.enums.TurnOnStatus;
import com.kadir.smartirrigation.motor.model.MotorState;
import com.kadir.smartirrigation.common.enums.MotorStatus;
import com.kadir.smartirrigation.motor.repository.MotorStateRepository;
import com.kadir.smartirrigation.motor.service.MotorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MotorServiceImpl implements MotorService {
    private final MotorStateRepository repository;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> autoOffTask;

    @Override
    public void updateStatus(OnOffDto dto, TurnOnStatus turnOnStatus) {
        MotorState state = handleDefaultMotorState();

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            MotorStatus status = parseMotorStatus(dto.getStatus());
            if (status != null) {
                state.setStatus(status);
                if (status == MotorStatus.ON) {
                    switch (turnOnStatus) {
                        case AUTO -> scheduleAutoTurnOff(state.getAutoDurationSeconds());
                        case MANUAL -> scheduleAutoTurnOff(state.getManualDurationSeconds());
                    }
                } else if (autoOffTask != null && !autoOffTask.isDone()) {
                    autoOffTask.cancel(true);
                }
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
    public void updateDuration(DurationDto dto) {
        MotorState state = handleDefaultMotorState();
        if (dto.getAutoDurationSeconds() != null) {
            state.setAutoDurationSeconds(dto.getAutoDurationSeconds());
        }
        if (dto.getManualDurationSeconds() != null) {
            state.setManualDurationSeconds(dto.getManualDurationSeconds());
        }
        state.setUpdatedAt(LocalDateTime.now());
        repository.save(state);
    }

    @Override
    public MotorStateDto getStatus() {
        MotorState state = handleDefaultMotorState();
        return new MotorStateDto(
                state.getStatus().name(),
                state.isAutoControlEnabled(),
                state.getAutoDurationSeconds(),
                state.getManualDurationSeconds()
        );
    }

    @Override
    public void runWithDuration(int seconds) {
        updateStatus(new OnOffDto("ON"), TurnOnStatus.SCHEDULED);

        scheduler.schedule(() -> {
            updateStatus(new OnOffDto("OFF"), TurnOnStatus.SCHEDULED);
        }, seconds, TimeUnit.SECONDS);
    }

    private MotorState handleDefaultMotorState() {
        return repository.findById(1L).orElseGet(() -> {
            MotorState newState = new MotorState();
            newState.setId(1L);
            newState.setStatus(MotorStatus.OFF);
            newState.setAutoControlEnabled(true);
            newState.setAutoDurationSeconds(10);
            newState.setManualDurationSeconds(10);
            newState.setUpdatedAt(LocalDateTime.now());
            repository.save(newState);
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

    private void scheduleAutoTurnOff(int seconds) {
        if (autoOffTask != null && !autoOffTask.isDone()) {
            autoOffTask.cancel(true);
        }
        System.out.println("duration: "+seconds);

        autoOffTask = scheduler.schedule(() -> {
            MotorState currentState = handleDefaultMotorState();
            if (currentState.getStatus() == MotorStatus.ON) {
                currentState.setStatus(MotorStatus.OFF);
                currentState.setUpdatedAt(LocalDateTime.now());
                repository.save(currentState);
            }
        }, seconds, TimeUnit.SECONDS);
    }
}