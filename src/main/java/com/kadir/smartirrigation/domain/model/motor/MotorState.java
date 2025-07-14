package com.kadir.smartirrigation.domain.model.motor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "motor_state")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MotorState {
    @Id
    private Long id = 1L;

    @Column(name = "auto_control")
    private boolean autoControl;

    @Column(name = "moisture_threshold")
    private float moistureThreshold;

    @Column(name = "auto_duration_seconds")
    private int autoDurationSeconds;

    @Column(name = "manual_duration_seconds")
    private int manualDurationSeconds;

    @Column(name = "is_on")
    private boolean isOn;
}
