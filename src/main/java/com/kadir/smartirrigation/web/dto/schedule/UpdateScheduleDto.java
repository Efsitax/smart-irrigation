package com.kadir.smartirrigation.web.dto.schedule;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record UpdateScheduleDto(
        @NotNull(message = "Time cannot be null")
        LocalTime time,

        Set<DayOfWeek> days,
        boolean repeatDaily,
        LocalDate specificDate,

        @Min(value = 1, message = "Duration must be at least 1 second")
        int durationInSeconds
) { }
