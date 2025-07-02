package com.kadir.smartirrigation.common.exception;

public class MotorNotFoundException extends RuntimeException {
    public MotorNotFoundException(String message) {
        super(message);
    }
}
