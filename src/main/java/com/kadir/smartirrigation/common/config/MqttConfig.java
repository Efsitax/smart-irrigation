package com.kadir.smartirrigation.common.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
    private static final String MQTT_BROKER_URL = "tcp://broker.hivemq.com:1883";

    @Bean
    public MqttClient mqttClient() throws MqttException {
        MqttClient client = new MqttClient(MQTT_BROKER_URL, MqttClient.generateClientId());
        client.connect();
        return client;
    }
}
