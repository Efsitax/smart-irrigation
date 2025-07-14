package com.kadir.smartirrigation.web.dto.motor;

import jakarta.validation.constraints.NotNull;

public record UpdateMotorStateRequestDto(
        @NotNull(message = "AutoControl option cannot be null")
        boolean autoControl,

        @NotNull(message = "Moisture Threshold option cannot be null")
        float moistureThreshold,

        int autoDurationSeconds,
        int manualDurationSeconds
) { }