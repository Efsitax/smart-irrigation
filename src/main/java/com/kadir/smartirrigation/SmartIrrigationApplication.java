package com.kadir.smartirrigation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartIrrigationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartIrrigationApplication.class, args);
    }

}
