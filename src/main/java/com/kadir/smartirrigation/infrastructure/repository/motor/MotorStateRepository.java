package com.kadir.smartirrigation.infrastructure.repository.motor;

import com.kadir.smartirrigation.domain.model.motor.MotorState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotorStateRepository extends JpaRepository<MotorState, Long> {
}
