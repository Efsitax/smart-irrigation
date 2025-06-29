package com.kadir.smartirrigation.sensor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataResponseDto {
    private int soilMoistureAdc;
    private int batteryPercent;
    private LocalDateTime timestamp;
}
