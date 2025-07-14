package com.kadir.smartirrigation.application.service.sensor;

import com.kadir.smartirrigation.common.exception.SensorDataNotFoundException;
import com.kadir.smartirrigation.domain.event.LowBatteryEvent;
import com.kadir.smartirrigation.web.dto.sensor.SensorDataDto;
import com.kadir.smartirrigation.domain.model.sensor.SensorData;
import com.kadir.smartirrigation.infrastructure.repository.sensor.SensorDataRepository;
import com.kadir.smartirrigation.domain.service.sensor.SensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorDataServiceImpl implements SensorDataService {
    private final SensorDataRepository repository;
    private final ApplicationEventPublisher publisher;

    @Override
    public SensorDataDto save(SensorDataDto dto) {
        SensorData previous = repository.findTopByOrderByTimestampDesc().orElse(null);

        SensorData newData = SensorData.builder()
                .soilMoisturePercent(dto.soilMoisturePercent())
                .batteryPercent(dto.batteryPercent())
                .timestamp(dto.timestamp())
                .build();
        repository.save(newData);

        checkBatteryThresholdCrossing(previous, newData);

        return toDto(newData);
    }

    private void checkBatteryThresholdCrossing(SensorData previous, SensorData current) {
        double prevBatt = (previous != null) ? previous.getBatteryPercent() : 100.0;
        double currBatt = current.getBatteryPercent();

        if (prevBatt > 20.0 && currBatt <= 20.0) {
            publisher.publishEvent(new LowBatteryEvent(currBatt));
        }
    }

    @Override
    public SensorDataDto getLatestSensorData() {
        return toDto(repository.findTopByOrderByTimestampDesc()
                .orElseThrow(() -> new SensorDataNotFoundException("Sensor data not found")));
    }

    private SensorDataDto toDto(SensorData data) {
        return new SensorDataDto(
                data.getSoilMoisturePercent(),
                data.getBatteryPercent(),
                data.getTimestamp()
        );
    }
}