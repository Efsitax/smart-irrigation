package com.kadir.smartirrigation.web.dto.temperature;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TemperatureConfigDto(
        @NotNull(message = "Threshold cannot be null")
        double threshold,

        @NotNull(message = "Extra seconds cannot be null")
        int extraSeconds,

        @NotNull(message = "Active cannot be null")
        boolean active,

        LocalDateTime updatedAt
) {
}
