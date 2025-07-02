package com.kadir.smartirrigation.web.controller;

import com.kadir.smartirrigation.domain.service.notification.DeviceTokenService;
import com.kadir.smartirrigation.web.dto.notification.DeviceTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device-tokens")
@RequiredArgsConstructor
public class DeviceTokenController {
    private final DeviceTokenService tokenService;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody DeviceTokenDto dto) {
        tokenService.saveToken(dto.token());
        return ResponseEntity.ok().build();
    }
}