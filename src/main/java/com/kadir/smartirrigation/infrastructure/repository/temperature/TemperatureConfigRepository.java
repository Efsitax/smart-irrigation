package com.kadir.smartirrigation.infrastructure.repository.temperature;

import com.kadir.smartirrigation.domain.model.temperature.TemperatureConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemperatureConfigRepository extends JpaRepository<TemperatureConfig, Long> {
}
