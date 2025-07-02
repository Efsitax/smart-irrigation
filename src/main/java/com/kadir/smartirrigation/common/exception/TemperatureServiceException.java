package com.kadir.smartirrigation.common.exception;

public class TemperatureServiceException extends RuntimeException {
    public TemperatureServiceException(String message) {
        super(message);
    }

    public TemperatureServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
