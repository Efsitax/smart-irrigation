package com.kadir.smartirrigation.motor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorState {
    @Id
    private Long Id = 1L;

    @Enumerated(EnumType.STRING)
    private MotorStatus status;

    private boolean autoControlEnabled;
    private LocalDateTime updatedAt;
}
