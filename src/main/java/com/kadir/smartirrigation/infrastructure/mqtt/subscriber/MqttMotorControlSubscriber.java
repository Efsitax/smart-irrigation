package com.kadir.smartirrigation.infrastructure.mqtt.subscriber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kadir.smartirrigation.domain.service.motor.MotorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttMotorControlSubscriber {

    private final MqttClient mqttClient;
    private final MotorService motorService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        try {
            // assume mqttClient is already connected
            mqttClient.subscribe("motor/control", 1, (topic, message) -> {
                // 1) Log the raw payload coming from ESP32
                String raw = new String(message.getPayload(), StandardCharsets.UTF_8).trim();
                log.info("[SUBSCRIBER] Raw motor/control payload: {}", raw);

                // 2) Try parsing JSON
                String command;
                try {
                    JsonNode node = objectMapper.readTree(raw);
                    command = node.get("command").asText();
                } catch (Exception e) {
                    log.warn("[SUBSCRIBER] Payload not valid JSON, ignoring: {}", raw);
                    return;
                }

                // 3) Handle OFF command
                if ("OFF".equalsIgnoreCase(command)) {
                    log.info("[SUBSCRIBER] Received OFF → updating backend state");
                    motorService.updateIsOn(false);
                }
            });
            log.info("Subscribed to motor/control");
        } catch (MqttException e) {
            log.error("Failed to subscribe to motor/control topic", e);
        }
    }
}