package com.kadir.smartirrigation.domain.service;

import com.kadir.smartirrigation.web.dto.sensor.SensorDataDto;

public interface SensorDataService {
    SensorDataDto save(SensorDataDto dto);
    SensorDataDto getLatestSensorData();
}
