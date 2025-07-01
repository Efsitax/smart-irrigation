package com.kadir.smartirrigation.infastructure.repository;

import com.kadir.smartirrigation.domain.model.MotorState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotorStateRepository extends JpaRepository<MotorState, Long> {
}
