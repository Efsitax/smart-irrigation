package com.kadir.smartirrigation.motor.repository;

import com.kadir.smartirrigation.motor.model.MotorState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotorStateRepository extends JpaRepository<MotorState, Long> {
}
