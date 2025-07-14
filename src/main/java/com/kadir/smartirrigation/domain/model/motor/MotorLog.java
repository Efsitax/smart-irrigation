package com.kadir.smartirrigation.domain.model.motor;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "motor_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MotorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "duration_seconds")
    private int durationSeconds;

    @Column(name = "moisture_at_trigger")
    private float moistureAtTrigger;

    private String mode;
}