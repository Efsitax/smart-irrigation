package com.kadir.smartirrigation.infrastructure.repository.motor;

import com.kadir.smartirrigation.domain.model.motor.MotorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotorLogRepository extends JpaRepository<MotorLog, Long> { }