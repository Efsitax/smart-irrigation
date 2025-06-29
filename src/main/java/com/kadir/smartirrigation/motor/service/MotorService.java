package com.kadir.smartirrigation.motor.service;

import com.kadir.smartirrigation.motor.dto.MotorStateDto;

public interface MotorService {
    void updateStatus(MotorStateDto dto);
    void updateAutoControl(boolean enabled);
    MotorStateDto getStatus();
}
