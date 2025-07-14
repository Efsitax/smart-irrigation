package com.kadir.smartirrigation.web.dto.sensor;

import java.time.LocalDateTime;

public record SensorDataDto(
        float soilMoisturePercent,
        float batteryPercent,
        LocalDateTime timestamp
) { }
