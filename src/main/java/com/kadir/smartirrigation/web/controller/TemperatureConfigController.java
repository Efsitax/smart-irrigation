package com.kadir.smartirrigation.web.controller;

import com.kadir.smartirrigation.domain.service.temperature.TemperatureConfigService;
import com.kadir.smartirrigation.web.dto.temperature.TemperatureConfigDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temperature")
@RequiredArgsConstructor
public class TemperatureConfigController {
    private final TemperatureConfigService temperatureConfigService;

    @GetMapping
    public ResponseEntity<TemperatureConfigDto> getTemperatureConfig() {
        return ResponseEntity.ok(temperatureConfigService.getConfig());
    }

    @PutMapping
    public ResponseEntity<TemperatureConfigDto> updateTemperatureConfig(@Valid @RequestBody TemperatureConfigDto dto) {
        return ResponseEntity.ok(temperatureConfigService.updateConfig(dto));
    }
}
