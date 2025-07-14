package com.kadir.smartirrigation.web.dto.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record ScheduleResponseDto (
        Long id,
        LocalTime time,
        Set<DayOfWeek> days,
        int durationInSeconds,
        boolean repeatDaily,
        LocalDate specificDate,
        boolean active
) { }
