package com.kadir.smartirrigation.motor.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class DurationDto {
    @Min(value = 3, message = "Auto duration must be at least 3 seconds")
    private Integer autoDurationSeconds;

    @Min(value = 3, message = "Manual duration must be at least 3 seconds")
    private Integer manualDurationSeconds;
}
