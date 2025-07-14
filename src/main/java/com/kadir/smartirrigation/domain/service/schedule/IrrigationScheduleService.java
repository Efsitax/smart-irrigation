package com.kadir.smartirrigation.domain.service.schedule;

import com.kadir.smartirrigation.web.dto.schedule.CreateScheduleDto;
import com.kadir.smartirrigation.web.dto.schedule.ScheduleResponseDto;
import com.kadir.smartirrigation.web.dto.schedule.UpdateScheduleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface IrrigationScheduleService {
    ScheduleResponseDto save(CreateScheduleDto dto);
    void delete(Long id);
    List<ScheduleResponseDto> getAll();
    List<ScheduleResponseDto> getActiveSchedulesFor(LocalDateTime dateTime);
    ScheduleResponseDto update(Long id, UpdateScheduleDto dto);
    void deactivateSchedule(Long id);
}
