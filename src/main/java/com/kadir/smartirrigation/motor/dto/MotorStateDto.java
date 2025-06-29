package com.kadir.smartirrigation.motor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorStateDto {
    private String status;
    private boolean autoControlEnabled;
    private Integer autoDurationSeconds;
    private Integer manualDurationSeconds;
}
