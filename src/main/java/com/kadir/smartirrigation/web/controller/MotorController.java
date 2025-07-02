package com.kadir.smartirrigation.web.controller;

import com.kadir.smartirrigation.domain.service.motor.MotorLogService;
import com.kadir.smartirrigation.web.dto.motor.*;
import com.kadir.smartirrigation.domain.service.motor.MotorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/motor")
@RequiredArgsConstructor
public class MotorController {
    private final MotorService motorService;
    private final MotorLogService motorLogService;

    @PostMapping("/manual")
    public ResponseEntity<Void> turnOnManual() {
        motorService.turnOnManual();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/state")
    public ResponseEntity<MotorStateDto> getState() {
        return ResponseEntity.ok(motorService.getCurrentState());
    }

    @PutMapping("/state")
    public ResponseEntity<Void> updateState(@Valid @RequestBody UpdateMotorStateRequestDto dto) {
        motorService.updateState(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logs")
    public ResponseEntity<List<MotorLogDto>> getLogs() {
        return ResponseEntity.ok(motorLogService.getMotorLogs());
    }
}
