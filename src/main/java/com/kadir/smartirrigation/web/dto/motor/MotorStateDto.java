package com.kadir.smartirrigation.web.dto.motor;

public record MotorStateDto(
        boolean autoControl,
        float moistureThreshold,
        int autoDurationSeconds,
        int manualDurationSeconds,
        boolean isOn
) { }