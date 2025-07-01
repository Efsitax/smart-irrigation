package com.kadir.smartirrigation.infastructure.mqtt.subscriber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kadir.smartirrigation.common.exception.MqttConnectionException;
import com.kadir.smartirrigation.domain.service.SensorDataService;
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

                } catch (Exception ex) {
                    log.error("MQTT data couldn't be processed", ex);
                }
            });
        } catch (MqttException e) {
            throw new MqttConnectionException("MQTT subscription failed.", e);
        }
    }


}
