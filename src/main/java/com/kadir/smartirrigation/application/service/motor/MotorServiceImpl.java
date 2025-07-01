package com.kadir.smartirrigation.application.service.motor;

import com.kadir.smartirrigation.domain.model.SensorData;
import com.kadir.smartirrigation.domain.service.MotorLogService;
import com.kadir.smartirrigation.infastructure.mqtt.publisher.MqttPublisherService;
import com.kadir.smartirrigation.infastructure.repository.SensorDataRepository;
import com.kadir.smartirrigation.web.dto.motor.MotorLogDto;
import com.kadir.smartirrigation.web.dto.motor.MotorStateDto;
import com.kadir.smartirrigation.domain.model.MotorState;
import com.kadir.smartirrigation.infastructure.repository.MotorStateRepository;
import com.kadir.smartirrigation.domain.service.MotorService;
import com.kadir.smartirrigation.web.dto.motor.UpdateMotorStateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MotorServiceImpl implements MotorService {
    private final MotorStateRepository motorStateRepository;
    private final MotorLogService motorLogService;
    private final SensorDataRepository sensorDataRepository;
    private final MqttPublisherService mqttPublisher;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    @Override
    public void evaluateAutoControl(float currentMoisturePercent) {
        MotorState state = motorStateRepository.findById(1L).orElseThrow();

        if (!state.isAutoControl()) {
            log.info("AutoControl is off, nothing to do.");
            return;
        }

        if (currentMoisturePercent < state.getMoistureThreshold()) {
            log.info("AutoControl: Moisture {}% < threshold {}% → Motor is on.", currentMoisturePercent, state.getMoistureThreshold());
            turnOnForDuration(state.getAutoDurationSeconds(), "AUTO", currentMoisturePercent);
        }
    }

    @Override
    public void turnOnForDuration(int seconds, String mode, float moisture) {
        log.info("Motor is on for {} seconds...", seconds);
        mqttPublisher.publish("motor/control", "ON");
        updateIsOn(true);

        motorLogService.saveMotorLog(new MotorLogDto(
                mode,
                LocalDateTime.now(),
                seconds,
                moisture
        ));

        scheduler.schedule(() -> {
            mqttPublisher.publish("motor/control", "OFF");
            updateIsOn(false);
            log.info("Time is up. Motor is off.");
        }, seconds, TimeUnit.SECONDS);
    }

    @Override
    public void turnOnManual() {
        MotorState state = motorStateRepository.findById(1L).orElseThrow();
        float latestMoisture = getLatestMoisture();
        log.info("Manuel mod: Motor is on ({} seconds).", state.getManualDurationSeconds());

        turnOnForDuration(state.getManualDurationSeconds(), "MANUAL", latestMoisture);
    }

    @Override
    public void updateState(UpdateMotorStateRequestDto dto) {
        MotorState state = motorStateRepository.findById(1L).orElseThrow();

        state.setAutoControl(dto.autoControl());
        state.setMoistureThreshold(dto.moistureThreshold());
        state.setAutoDurationSeconds(dto.autoDurationSeconds());
        state.setManualDurationSeconds(dto.manualDurationSeconds());

        motorStateRepository.save(state);
    }

    @Override
    public MotorStateDto getCurrentState() {
        MotorState state = motorStateRepository.findById(1L).orElseThrow();

        return new MotorStateDto(
                state.isAutoControl(),
                state.getMoistureThreshold(),
                state.getAutoDurationSeconds(),
                state.getManualDurationSeconds(),
                state.isOn()
        );

    }

    private void updateIsOn(boolean isOn) {
        MotorState state = motorStateRepository.findById(1L).orElseThrow();
        state.setOn(isOn);
        motorStateRepository.save(state);
    }

    private float getLatestMoisture() {
        SensorData latest = sensorDataRepository.findTopByOrderByTimestampDesc().orElse(null);
        return latest != null ? latest.getSoilMoisturePercent() : 0f;
    }
}