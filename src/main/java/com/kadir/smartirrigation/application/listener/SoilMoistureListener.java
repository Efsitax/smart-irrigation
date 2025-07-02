package com.kadir.smartirrigation.application.listener;

import com.kadir.smartirrigation.domain.event.LowSoilMoistureEvent;
import com.kadir.smartirrigation.domain.model.notification.NotificationType;
import com.kadir.smartirrigation.domain.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoilMoistureListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleLowSoilMoisture(LowSoilMoistureEvent event) {
        String title = "Critical Soil Moisture";
        String body  = String.format("Soil moisture dropped to %.1f%%", event.moistureLevel());
        notificationService.send(
                NotificationType.LOW_MOISTURE,
                null,
                title,
                body
        );
    }
}