package com.kadir.smartirrigation.infastructure.repository;

import com.kadir.smartirrigation.domain.model.MotorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotorLogRepository extends JpaRepository<MotorLog, Long> { }