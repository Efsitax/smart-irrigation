package com.kadir.smartirrigation.sensor.controller;

import com.kadir.smartirrigation.sensor.dto.SensorlDataRequestDto;
import com.kadir.smartirrigation.sensor.dto.SensorDataResponseDto;
import com.kadir.smartirrigation.sensor.service.SensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor-data")
@RequiredArgsConstructor
public class SensorDataController {
    public final SensorDataService service;

    @PostMapping
    public ResponseEntity<SensorDataResponseDto> save(@RequestBody SensorlDataRequestDto dto) {
        return ResponseEntity.ok(service.saveData(dto));
    }

    @GetMapping("/latest")
    public ResponseEntity<SensorDataResponseDto> getLatest() {
        return ResponseEntity.ok(service.getLatestData());
    }
}
