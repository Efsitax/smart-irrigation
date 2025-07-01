package com.kadir.smartirrigation.motor.service;

import com.kadir.smartirrigation.motor.dto.DurationDto;
import com.kadir.smartirrigation.motor.dto.MotorStateDto;
import com.kadir.smartirrigation.motor.dto.OnOffDto;
import com.kadir.smartirrigation.common.enums.TurnOnStatus;

public interface MotorService {
    void updateStatus(OnOffDto dto, TurnOnStatus turnOnStatus);
    void updateAutoControl(boolean enabled);
    void updateDuration(DurationDto dto);
    MotorStateDto getStatus();

    void runWithDuration(int seconds);
}
