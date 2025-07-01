package com.kadir.smartirrigation.schedule.service.impl;

import com.kadir.smartirrigation.schedule.dto.CreateScheduleDto;
import com.kadir.smartirrigation.schedule.dto.ScheduleResponseDto;
import com.kadir.smartirrigation.schedule.exception.ScheduleNotFoundException;
import com.kadir.smartirrigation.schedule.model.IrrigationSchedule;
import com.kadir.smartirrigation.schedule.repository.IrrigationScheduleRepository;
import com.kadir.smartirrigation.schedule.service.IrrigaitionScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IrrigationScheduleServiceImpl implements IrrigaitionScheduleService {
    private final IrrigationScheduleRepository repository;

    @Override
    public ScheduleResponseDto save(CreateScheduleDto dto) {
        IrrigationSchedule schedule = new IrrigationSchedule().builder()
                .time(dto.getTime())
                .days(dto.getDays())
                .durationInSeconds(dto.getDurationInSeconds())
                .repeatDaily(dto.isRepeatDaily())
                .specificDate(dto.getSpecificDate())
                .active(true)
                .build();

        return toDto(repository.save(schedule));
    }

    @Override
    public void delete(Long id) {
        IrrigationSchedule schedule = repository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));
        repository.delete(schedule);
    }

    @Override
    public List<ScheduleResponseDto> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponseDto> getActiveSchedulesFor(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime().withSecond(0).withNano(0);
        DayOfWeek day = dateTime.getDayOfWeek();
        LocalDate date = dateTime.toLocalDate();

        return repository.findScheduleForTime(time, day, date).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ScheduleResponseDto toDto(IrrigationSchedule schedule) {
        ScheduleResponseDto dto = new ScheduleResponseDto();
        dto.setId(schedule.getId());
        dto.setTime(schedule.getTime());
        dto.setDays(schedule.getDays());
        dto.setDurationInSeconds(schedule.getDurationInSeconds());
        dto.setRepeatDaily(schedule.isRepeatDaily());
        dto.setSpecificDate(schedule.getSpecificDate());
        dto.setActive(schedule.isActive());
        return dto;
    }
}
