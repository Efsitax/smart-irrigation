package com.kadir.smartirrigation.application.service.schedule;

import com.kadir.smartirrigation.web.dto.schedule.CreateScheduleDto;
import com.kadir.smartirrigation.web.dto.schedule.ScheduleResponseDto;
import com.kadir.smartirrigation.common.exception.ScheduleNotFoundException;
import com.kadir.smartirrigation.domain.model.schedule.IrrigationSchedule;
import com.kadir.smartirrigation.infrastructure.repository.schedule.IrrigationScheduleRepository;
import com.kadir.smartirrigation.domain.service.schedule.IrrigationScheduleService;
import com.kadir.smartirrigation.web.dto.schedule.UpdateScheduleDto;
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
public class IrrigationScheduleServiceImpl implements IrrigationScheduleService {
    private final IrrigationScheduleRepository repository;

    @Override
    public ScheduleResponseDto save(CreateScheduleDto dto) {
        IrrigationSchedule schedule = IrrigationSchedule.builder()
                .time(dto.time())
                .days(dto.days())
                .durationInSeconds(dto.durationInSeconds())
                .repeatDaily(dto.repeatDaily())
                .specificDate(dto.specificDate())
                .active(true)
                .build();

        return toDto(repository.save(schedule));
    }

    @Override
    public void delete(Long id) {
        IrrigationSchedule schedule = getScheduleOrThrow(id);
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

    @Override
    public ScheduleResponseDto update(Long id, UpdateScheduleDto dto) {
        IrrigationSchedule schedule = getScheduleOrThrow(id);

        schedule.setTime(dto.time());
        schedule.setDays(dto.days());
        schedule.setRepeatDaily(dto.repeatDaily());
        schedule.setSpecificDate(dto.specificDate());
        schedule.setDurationInSeconds(dto.durationInSeconds());

        return toDto(repository.save(schedule));
    }

    @Override
    public void deactivateSchedule(Long id) {
        IrrigationSchedule schedule = getScheduleOrThrow(id);
        schedule.setActive(false);
        repository.save(schedule);
    }

    public IrrigationSchedule getScheduleOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));
    }

    private ScheduleResponseDto toDto(IrrigationSchedule schedule) {
        return new ScheduleResponseDto(
                schedule.getId(),
                schedule.getTime(),
                schedule.getDays(),
                schedule.getDurationInSeconds(),
                schedule.isRepeatDaily(),
                schedule.getSpecificDate(),
                schedule.isActive()
        );
    }
}
