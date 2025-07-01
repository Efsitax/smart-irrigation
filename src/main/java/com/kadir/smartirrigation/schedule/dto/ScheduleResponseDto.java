package com.kadir.smartirrigation.schedule.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
public class ScheduleResponseDto {
    private Long id;
    private LocalTime time;
    private Set<DayOfWeek> days;
    private int durationInSeconds;
    private boolean repeatDaily;
    private LocalDate specificDate;
    private boolean active;
}
