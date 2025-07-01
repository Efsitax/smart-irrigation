package com.kadir.smartirrigation.motor.controller;

import com.kadir.smartirrigation.motor.dto.AutoControlRequestDto;
import com.kadir.smartirrigation.motor.dto.DurationDto;
import com.kadir.smartirrigation.motor.dto.MotorStateDto;
import com.kadir.smartirrigation.motor.dto.OnOffDto;
import com.kadir.smartirrigation.common.enums.TurnOnStatus;
import com.kadir.smartirrigation.motor.service.MotorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/motor")
@RequiredArgsConstructor
public class MotorController {
    private final MotorService service;
    private final MotorService motorService;

    @PutMapping
    public ResponseEntity<String> update(@Valid @RequestBody OnOffDto dto) {
        service.updateStatus(dto, TurnOnStatus.MANUAL);
        return ResponseEntity.ok("Motor state updated: " + dto.getStatus());
    }

    @PutMapping("/auto-control")
    public ResponseEntity<Void> updateAutoControl(@RequestBody AutoControlRequestDto dto) {
        motorService.updateAutoControl(dto.isEnabled());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/duration")
    public void updateDuration(@Valid @RequestBody DurationDto dto) {
        motorService.updateDuration(dto);
    }

    @GetMapping
    public ResponseEntity<MotorStateDto> getStatus() {
        return ResponseEntity.ok(service.getStatus());
    }

}
