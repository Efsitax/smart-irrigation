package com.kadir.smartirrigation.domain.model.sensor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "soil_moisture_percent")
    private float soilMoisturePercent;

    @Column(name = "battery_percent")
    private float batteryPercent;

    private LocalDateTime timestamp;
}
