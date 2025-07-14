package com.kadir.smartirrigation.web.controller;

import com.kadir.smartirrigation.domain.model.notification.NotificationType;
import com.kadir.smartirrigation.domain.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/test")
    public ResponseEntity<String> sendTestNotification() {
        notificationService.send(
                NotificationType.IRRIGATION_STARTED,
                null,
                "Test Notification",
                "This is a test push notification."
        );
        return ResponseEntity.ok("Test notification sent");
    }
}