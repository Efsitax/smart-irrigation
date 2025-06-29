package com.kadir.smartirrigation.sensor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorlDataRequestDto {
    private int soilMoistureAdc;
    private int batteryPercent;
}
