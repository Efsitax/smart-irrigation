package com.kadir.smartirrigation.sensor.service;

import com.kadir.smartirrigation.sensor.dto.SensorlDataRequestDto;
import com.kadir.smartirrigation.sensor.dto.SensorDataResponseDto;

public interface SensorDataService {
    SensorDataResponseDto saveData(SensorlDataRequestDto requestDto);
    SensorDataResponseDto getLatestData();
}
