package com.kadir.smartirrigation.schedule.service;

import com.kadir.smartirrigation.schedule.dto.CreateScheduleDto;
import com.kadir.smartirrigation.schedule.dto.ScheduleResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface IrrigaitionScheduleService {
    ScheduleResponseDto save(CreateScheduleDto dto);
    void delete(Long id);
    List<ScheduleResponseDto> getAll();
    List<ScheduleResponseDto> getActiveSchedulesFor(LocalDateTime dateTime);
}
