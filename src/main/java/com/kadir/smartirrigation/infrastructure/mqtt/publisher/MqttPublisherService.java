package com.kadir.smartirrigation.infrastructure.mqtt.publisher;

import com.kadir.smartirrigation.common.exception.MqttConnectionException;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MqttPublisherService {
    private final MqttClient mqttClient;

    public void publish(String topic, String payload) {
        try {
            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }
            MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            message.setQos(1);           // ensure delivery at least once
            message.setRetained(false);  // do not retain so stale commands aren't replayed
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            throw new MqttConnectionException("Failed to publish message: " + topic, e);
        }
    }
}