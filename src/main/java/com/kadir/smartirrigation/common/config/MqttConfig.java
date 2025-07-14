package com.kadir.smartirrigation.common.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Bean
    public MqttClient mqttClient() throws Exception {
        MqttClient client = new MqttClient("tcp://broker.hivemq.com:1883", "backendClient", new MemoryPersistence());
        client.connect();
        return client;
    }
}