package com.kadir.smartirrigation.infrastructure.repository.sensor;

import com.kadir.smartirrigation.domain.model.sensor.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    Optional<SensorData> findTopByOrderByTimestampDesc();
}
