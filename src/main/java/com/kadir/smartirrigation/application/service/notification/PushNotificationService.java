package com.kadir.smartirrigation.application.service.notification;

import com.google.firebase.messaging.*;
import com.kadir.smartirrigation.common.exception.NotificationException;
import com.kadir.smartirrigation.domain.model.notification.NotificationType;
import com.kadir.smartirrigation.domain.service.notification.DeviceTokenService;
import com.kadir.smartirrigation.domain.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationService implements NotificationService {
    private final DeviceTokenService tokenService;

    @Override
    @Async
    public void send(NotificationType type, String recipient, String title, String body) {
        List<String> tokens = tokenService.getAllTokens();
        if (tokens.isEmpty()) {
            log.info("No device tokens registered – skipping push.");
            return;
        }

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .putData("type", type.name())
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            BatchResponse response = FirebaseMessaging
                    .getInstance()
                    .sendEachForMulticast(message);

            log.info("Push notifications: {} succeeded, {} failed",
                    response.getSuccessCount(),
                    response.getFailureCount());

            List<SendResponse> results = response.getResponses();
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).isSuccessful()) {
                    String badToken = tokens.get(i);
                    tokenService.deleteToken(badToken);
                    log.warn("Removed invalid token: {}", badToken);
                }
            }
        } catch (FirebaseMessagingException e) {
            throw new NotificationException("Push notification could not be sent", e);
        }
    }
}