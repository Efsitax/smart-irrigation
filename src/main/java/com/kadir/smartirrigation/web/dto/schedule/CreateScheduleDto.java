package com.kadir.smartirrigation.web.dto.schedule;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record CreateScheduleDto (
        @NotNull(message = "Time is required")
        LocalTime time,

        Set<DayOfWeek> days,
        @Min(value = 1, message = "Duration must be at least 1 seconds")
        int durationInSeconds,
        boolean repeatDaily,

        @FutureOrPresent(message = "Specific date must be today or in the future")
        LocalDate specificDate
) { }
