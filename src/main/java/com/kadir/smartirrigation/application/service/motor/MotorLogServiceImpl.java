package com.kadir.smartirrigation.application.service.motor;

import com.kadir.smartirrigation.domain.model.motor.MotorLog;
import com.kadir.smartirrigation.domain.service.motor.MotorLogService;
import com.kadir.smartirrigation.infrastructure.repository.motor.MotorLogRepository;
import com.kadir.smartirrigation.web.dto.motor.MotorLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MotorLogServiceImpl implements MotorLogService {
    private final MotorLogRepository motorLogRepository;

    @Override
    public List<MotorLogDto> getMotorLogs() {
        return motorLogRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveMotorLog(MotorLogDto dto) {
        motorLogRepository.save(MotorLog.builder()
                .mode(dto.mode())
                .startTime(dto.startTime())
                .durationSeconds(dto.durationSeconds())
                .moistureAtTrigger(dto.moistureAtTrigger())
                .build());
    }

    private MotorLogDto toDto(MotorLog log) {
        return new MotorLogDto(
                log.getId(),
                log.getMode(),
                log.getStartTime(),
                log.getDurationSeconds(),
                log.getMoistureAtTrigger()
        );
    }
}
