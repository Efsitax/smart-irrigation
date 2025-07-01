package com.kadir.smartirrigation.common.exception;

public class MqttConnectionException extends RuntimeException {
    public MqttConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
