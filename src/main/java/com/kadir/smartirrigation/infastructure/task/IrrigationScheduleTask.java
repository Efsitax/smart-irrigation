package com.kadir.smartirrigation.infastructure.task;

import com.kadir.smartirrigation.domain.service.MotorService;
import com.kadir.smartirrigation.domain.service.SensorDataService;
import com.kadir.smartirrigation.web.dto.schedule.ScheduleResponseDto;
import com.kadir.smartirrigation.domain.service.IrrigationScheduleService;
import com.kadir.smartirrigation.web.dto.sensor.SensorDataDto;
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
    private final IrrigationScheduleService scheduleService;
    private final MotorService motorService;
    private final SensorDataService sensorDataService;

    @Scheduled(cron = "0 * * * * *")
    public void checkAndTriggerSchedules() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        List<ScheduleResponseDto> schedules = scheduleService.getActiveSchedulesFor(now);

        if (schedules.isEmpty()) {
            return;
        }

        float moisture = getLatestMoisture();

        for (ScheduleResponseDto schedule : schedules) {
            log.info("Scheduled irrigation triggered: ID={}, Time={}, Duration={}sn", schedule.id(), schedule.time(), schedule.durationInSeconds());
            motorService.turnOnForDuration(schedule.durationInSeconds(), "SCHEDULED", moisture);
        }
    }

    private float getLatestMoisture() {
        SensorDataDto latest = sensorDataService.getLatestSensorData();
        return latest != null ? latest.soilMoisturePercent() : 0f;
    }
}
