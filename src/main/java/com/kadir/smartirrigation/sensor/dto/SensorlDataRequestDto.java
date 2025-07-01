package com.kadir.smartirrigation.sensor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorlDataRequestDto {
    @Min(value = 0, message = "Soil moisture ADC value cannot be negative")
    private int soilMoistureAdc;

    @Min(value = 0, message = "Battery percent must be at least 0")
    @Max(value = 100, message = "Battery percent cannot exceed 100")
    private int batteryPercent;
}
