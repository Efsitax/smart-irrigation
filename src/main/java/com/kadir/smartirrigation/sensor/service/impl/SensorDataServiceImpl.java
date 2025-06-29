package com.kadir.smartirrigation.sensor.service.impl;

import com.kadir.smartirrigation.motor.dto.MotorStateDto;
import com.kadir.smartirrigation.motor.service.MotorService;
import com.kadir.smartirrigation.sensor.dto.SensorlDataRequestDto;
import com.kadir.smartirrigation.sensor.dto.SensorDataResponseDto;
import com.kadir.smartirrigation.sensor.model.SensorData;
import com.kadir.smartirrigation.sensor.repository.SensorDataRepository;
import com.kadir.smartirrigation.sensor.service.SensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SensorDataServiceImpl implements SensorDataService {
    private final SensorDataRepository repository;
    private final MotorService motorService;

    @Value("${irrigation.threshold}")
    private int threshold;

    @Override
    public SensorDataResponseDto saveData(SensorlDataRequestDto requestDto) {
        SensorData entity = new SensorData();
        entity.setSoilMoistureAdc(requestDto.getSoilMoistureAdc());
        entity.setBatteryPercent(requestDto.getBatteryPercent());
        entity.setTimestamp(LocalDateTime.now());

        SensorData saved = repository.save(entity);

        evaluateAndControlMotor(saved);

        return new SensorDataResponseDto(
                saved.getSoilMoistureAdc(),
                saved.getBatteryPercent(),
                saved.getTimestamp()
        );
    }

    @Override
    public SensorDataResponseDto getLatestData() {
        SensorData data = repository.findTopByOrderByTimestampDesc().orElse(null);
        if (data == null) return null;
        return new SensorDataResponseDto(
                data.getSoilMoistureAdc(),
                data.getBatteryPercent(),
                data.getTimestamp()
        );
    }

    private void evaluateAndControlMotor(SensorData sensorData) {
        MotorStateDto currentState = motorService.getStatus();

        if (!currentState.isAutoControlEnabled()) return;

        MotorStateDto newState = new MotorStateDto();
        newState.setAutoControlEnabled(true);

        if (sensorData.getSoilMoistureAdc() > threshold) {
            System.out.println("buraya girdi");
            newState.setStatus("ON");
        } else {
            newState.setStatus("OFF");
        }

        motorService.updateStatus(newState);
    }
}
