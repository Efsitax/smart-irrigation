package com.kadir.smartirrigation.infastructure.repository;

import com.kadir.smartirrigation.domain.model.TemperatureConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemperatureConfigRepository extends JpaRepository<TemperatureConfig, Long> {
}
