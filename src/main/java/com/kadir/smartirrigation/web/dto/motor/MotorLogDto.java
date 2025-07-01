package com.kadir.smartirrigation.web.dto.motor;

import java.time.LocalDateTime;

public record MotorLogDto(
        String mode,
        LocalDateTime startTime,
        int durationSeconds,
        float moistureAtTrigger
) { }
