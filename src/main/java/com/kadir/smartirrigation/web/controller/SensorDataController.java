package com.kadir.smartirrigation.web.controller;

import com.kadir.smartirrigation.web.dto.sensor.SensorDataDto;
import com.kadir.smartirrigation.domain.service.sensor.SensorDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor-data")
@RequiredArgsConstructor
public class SensorDataController {
    public final SensorDataService service;

    @PostMapping
    public ResponseEntity<SensorDataDto> save(@Valid @RequestBody SensorDataDto dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping("/latest")
    public ResponseEntity<SensorDataDto> getLatest() {
        return ResponseEntity.ok(service.getLatestSensorData());
    }
}
