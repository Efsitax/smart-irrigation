package com.kadir.smartirrigation.domain.service.motor;

import com.kadir.smartirrigation.web.dto.motor.MotorLogDto;

import java.util.List;

public interface MotorLogService {
    List<MotorLogDto> getMotorLogs();
    void saveMotorLog(MotorLogDto dto);
}
