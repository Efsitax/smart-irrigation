package com.kadir.smartirrigation.application.service.motor;

import com.kadir.smartirrigation.common.exception.MotorNotFoundException;
import com.kadir.smartirrigation.domain.event.LowSoilMoistureEvent;
import com.kadir.smartirrigation.domain.service.motor.MotorLogService;
import com.kadir.smartirrigation.domain.service.temperature.TemperatureConfigService;
import com.kadir.smartirrigation.infrastructure.mqtt.publisher.MqttPublisherService;
import com.kadir.smartirrigation.web.dto.motor.MotorLogDto;
import com.kadir.smartirrigation.web.dto.motor.MotorStateDto;
import com.kadir.smartirrigation.web.dto.motor.UpdateMotorStateRequestDto;
import com.kadir.smartirrigation.domain.model.motor.MotorState;
import com.kadir.smartirrigation.infrastructure.repository.motor.MotorStateRepository;
import com.kadir.smartirrigation.domain.service.motor.MotorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MotorServiceImpl implements MotorService {

    private final MotorStateRepository motorStateRepository;
    private final MotorLogService motorLogService;
    private final TemperatureConfigService temperatureConfigService;
    private final MqttPublisherService mqttPublisher;
    private final ApplicationEventPublisher publisher;

    @Override
    public void evaluateAutoControl(float currentMoisturePercent) {
        MotorState state = getMotorOrThrow();

        if (!state.isAutoControl()) {
            publisher.publishEvent(new LowSoilMoistureEvent(currentMoisturePercent));
            return;
        }
        if (state.isOn()) {
            return;
        }


        float threshold = state.getMoistureThreshold();
        if (currentMoisturePercent < threshold) {
            turnOnForDuration(state.getAutoDurationSeconds(), "AUTO", currentMoisturePercent);
        }
    }

    @Override
    public void turnOnForDuration(int seconds, String mode, float moisture) {
        int finalSeconds = seconds;
        if (temperatureConfigService.evaluateTemperature()) {
            int extra = temperatureConfigService.getConfig().extraSeconds();
            finalSeconds += extra;
            log.info("Added {} extra seconds due to temperature.", extra);
        }

        log.info("Turning motor ON for {} seconds...", finalSeconds);

        String payload = String.format("{\"command\":\"ON\",\"duration\":%d}", finalSeconds);
        mqttPublisher.publish("motor/control", payload);

        updateIsOn(true);

        motorLogService.saveMotorLog(
                new MotorLogDto(null, mode, LocalDateTime.now(), finalSeconds, moisture)
        );
    }

    @Override
    public void turnOnManual() {
        MotorState state = getMotorOrThrow();
        turnOnForDuration(state.getManualDurationSeconds(), "MANUAL", 0f);
    }

    @Override
    public void updateState(UpdateMotorStateRequestDto dto) {
        MotorState state = getMotorOrThrow();
        state.setAutoControl(dto.autoControl());
        state.setMoistureThreshold(dto.moistureThreshold());
        state.setAutoDurationSeconds(dto.autoDurationSeconds());
        state.setManualDurationSeconds(dto.manualDurationSeconds());
        motorStateRepository.save(state);
    }

    @Override
    public void updateIsOn(boolean isOn) {
        MotorState state = getMotorOrThrow();
        state.setOn(isOn);
        motorStateRepository.save(state);
    }

    @Override
    public MotorStateDto getCurrentState() {
        MotorState s = getMotorOrThrow();
        return new MotorStateDto(
                s.isAutoControl(),
                s.getMoistureThreshold(),
                s.getAutoDurationSeconds(),
                s.getManualDurationSeconds(),
                s.isOn()
        );
    }

    private MotorState getMotorOrThrow() {
        return motorStateRepository.findById(1L)
                .orElseThrow(() -> new MotorNotFoundException("Motor not found"));
    }
}