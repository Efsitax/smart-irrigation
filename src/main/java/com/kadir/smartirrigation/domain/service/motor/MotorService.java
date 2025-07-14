package com.kadir.smartirrigation.domain.service.motor;

import com.kadir.smartirrigation.web.dto.motor.MotorStateDto;
import com.kadir.smartirrigation.web.dto.motor.UpdateMotorStateRequestDto;

public interface MotorService {
    void evaluateAutoControl(float currentMoisturePercent);
    void turnOnForDuration(int seconds, String mode, float moisture);
    void turnOnManual();
    void updateState(UpdateMotorStateRequestDto dto);
    void updateIsOn(boolean isOn);
    MotorStateDto getCurrentState();
}
