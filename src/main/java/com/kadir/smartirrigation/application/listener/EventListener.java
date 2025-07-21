package com.kadir.smartirrigation.application.listener;

import com.kadir.smartirrigation.domain.event.LowBatteryEvent;
import com.kadir.smartirrigation.domain.event.LowSoilMoistureEvent;
import com.kadir.smartirrigation.domain.event.ScheduledIrrigationEvent;
import com.kadir.smartirrigation.domain.model.notification.NotificationType;
import com.kadir.smartirrigation.domain.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class EventListener {

    private final NotificationService notificationService;
    private final Map<NotificationType, LocalDateTime> lastSentAt = new ConcurrentHashMap<>();


    @org.springframework.context.event.EventListener
    public void handleLowSoilMoisture(LowSoilMoistureEvent event) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last = lastSentAt.get(NotificationType.LOW_MOISTURE);

        if (last == null || Duration.between(last, now).toHours() >= 1) {
            String title = "Critical Soil Moisture";
            String body  = String.format("Soil moisture dropped to %.1f%%", event.moistureLevel());
            notificationService.send(
                    NotificationType.LOW_MOISTURE,
                    null,
                    title,
                    body
            );
            lastSentAt.put(NotificationType.LOW_MOISTURE, now);
        }
    }

    @org.springframework.context.event.EventListener
    public void handleLowBattery(LowBatteryEvent event) {
        String title = "Low Battery";
        String body = String.format("Battery low. Battery dropped to %.1f%%", event.batteryLevel());
        notificationService.send(
                NotificationType.LOW_BATTERY,
                null,
                title,
                body
        );
    }

    @org.springframework.context.event.EventListener
    public void handleScheduledIrrigation(ScheduledIrrigationEvent event) {
        String title = "Scheduled Irrigation";
        String formattedTime = event.time().format(DateTimeFormatter.ofPattern("HH:mm"));
        String body = String.format("Scheduled irrigation started. At %s Duration %d seconds", formattedTime, event.duration());
        notificationService.send(
                NotificationType.SCHEDULED,
                null,
                title,
                body
        );
    }
}