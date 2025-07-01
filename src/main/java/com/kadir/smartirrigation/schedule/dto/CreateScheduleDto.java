package com.kadir.smartirrigation.schedule.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
public class CreateScheduleDto {
    @NotNull(message = "Time is required")
    private LocalTime time;

    private Set<DayOfWeek> days;

    @Min(value = 3, message = "Duration must be at least 3 seconds")
    private int durationInSeconds;
    private boolean repeatDaily;

    @FutureOrPresent(message = "Specific date must be today or in the future")
    private LocalDate specificDate;
}
