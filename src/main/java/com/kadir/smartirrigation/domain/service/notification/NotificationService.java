package com.kadir.smartirrigation.domain.service.notification;

import com.kadir.smartirrigation.domain.model.notification.NotificationType;

public interface NotificationService {
    void send(NotificationType type, String recipient, String title, String body);
}
