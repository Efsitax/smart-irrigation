package com.kadir.smartirrigation.infastructure.scheduler;

import com.kadir.smartirrigation.domain.model.SensorData;
import com.kadir.smartirrigation.domain.service.MotorService;
import com.kadir.smartirrigation.infastructure.repository.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoControlEvaluatorTask {
    private final SensorDataRepository sensorDataRepository;
    private final MotorService motorService;

    @Scheduled(fixedRate = 30000)
    public void evaluate() {
        SensorData latest = sensorDataRepository.findTopByOrderByTimestampDesc().orElse(null);
        if (latest == null) return;

        log.info("Auto control evaluation: Latest moisture = {}%", latest.getSoilMoisturePercent());

        motorService.evaluateAutoControl(latest.getSoilMoisturePercent());
    }
}
