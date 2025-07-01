package com.kadir.smartirrigation.schedule.task;

import com.kadir.smartirrigation.motor.service.MotorService;
import com.kadir.smartirrigation.schedule.dto.ScheduleResponseDto;
import com.kadir.smartirrigation.schedule.service.IrrigaitionScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class IrrigationScheduleTask {
    private final IrrigaitionScheduleService scheduleService;
    private final MotorService motorService;

    @Scheduled(cron = "0 * * * * *")
    public void checkAndTriggerSchedules() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        List<ScheduleResponseDto> schedules = scheduleService.getActiveSchedulesFor(now);
        for (ScheduleResponseDto schedule : schedules) {
            log.info("Scheduled irrigation triggered: ID={}, Time={}", schedule.getId(), schedule.getTime());
            motorService.runWithDuration(schedule.getDurationInSeconds());
        }
    }
}
