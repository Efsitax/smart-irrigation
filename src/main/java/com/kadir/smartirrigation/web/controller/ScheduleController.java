package com.kadir.smartirrigation.web.controller;

import com.kadir.smartirrigation.web.dto.schedule.CreateScheduleDto;
import com.kadir.smartirrigation.web.dto.schedule.ScheduleResponseDto;
import com.kadir.smartirrigation.domain.service.schedule.IrrigationScheduleService;
import com.kadir.smartirrigation.web.dto.schedule.UpdateScheduleDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final IrrigationScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(@Valid @RequestBody CreateScheduleDto dto) {
        return ResponseEntity.ok(scheduleService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(@PathVariable Long id,@Valid @RequestBody UpdateScheduleDto dto) {
        return ResponseEntity.ok(scheduleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateSchedule(@PathVariable Long id) {
        scheduleService.deactivateSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
