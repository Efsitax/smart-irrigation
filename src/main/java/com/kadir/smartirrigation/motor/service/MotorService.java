package com.kadir.smartirrigation.motor.service;

import com.kadir.smartirrigation.motor.dto.DurationDto;
import com.kadir.smartirrigation.motor.dto.MotorStateDto;
import com.kadir.smartirrigation.motor.dto.OnOffDto;

public interface MotorService {
    void updateStatus(OnOffDto dto, boolean isAutoControl);
    void updateAutoControl(boolean enabled);
    void updateDuration(DurationDto dto);
    MotorStateDto getStatus();
}
