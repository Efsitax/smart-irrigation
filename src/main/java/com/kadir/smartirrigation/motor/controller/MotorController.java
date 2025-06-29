package com.kadir.smartirrigation.motor.controller;

import com.kadir.smartirrigation.motor.dto.AutoControlRequestDto;
import com.kadir.smartirrigation.motor.dto.MotorStateDto;
import com.kadir.smartirrigation.motor.service.MotorService;
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
    public ResponseEntity<String> update(@RequestBody MotorStateDto dto) {
        service.updateStatus(dto);
        return ResponseEntity.ok("Motor state updated: " + dto.getStatus());
    }

    @PutMapping("/auto-control")
    public ResponseEntity<Void> updateAutoControl(@RequestBody AutoControlRequestDto dto) {
        motorService.updateAutoControl(dto.isEnabled());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<MotorStateDto> getStatus() {
        return ResponseEntity.ok(service.getStatus());
    }

}
