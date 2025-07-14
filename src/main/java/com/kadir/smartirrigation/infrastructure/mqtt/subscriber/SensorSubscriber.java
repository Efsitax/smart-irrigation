package com.kadir.smartirrigation.infrastructure.mqtt.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kadir.smartirrigation.common.exception.MqttConnectionException;
import com.kadir.smartirrigation.common.exception.SensorDataFormatException;
import com.kadir.smartirrigation.domain.service.motor.MotorService;
import com.kadir.smartirrigation.domain.service.sensor.SensorDataService;
import com.kadir.smartirrigation.web.dto.sensor.SensorDataDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SensorSubscriber {
    private final MqttClient mqttClient;
    private final SensorDataService sensorDataService;
    private final MotorService motorService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init(){
        try {
            mqttClient.subscribe("sensor/soilData", (topic, message) -> {
                try {
                    String jsonPayload = new String(message.getPayload());
                    JsonNode node = objectMapper.readTree(jsonPayload);

                    if (!node.has("moisture") || !node.has("battery")) {
                        log.warn("Missing expected fields in MQTT payload: {}", jsonPayload);
                        return;
                    }

                    float moisture = (float) node.get("moisture").asDouble();
                    float battery = (float) node.get("battery").asDouble();

                    SensorDataDto dto = new SensorDataDto(moisture, battery, LocalDateTime.now());
                    sensorDataService.save(dto);

                    log.info("Received MQTT Data - Moisture: {}%, Battery: {}%", moisture, battery);
                    motorService.evaluateAutoControl(moisture);


                } catch (NullPointerException | JsonProcessingException e) {
                    throw new SensorDataFormatException("Invalid MQTT sensor data: " + e.getMessage());
                }
            });
        } catch (MqttException e) {
            throw new MqttConnectionException("MQTT subscription failed.", e);
        }
    }


}
