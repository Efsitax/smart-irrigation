package com.kadir.smartirrigation.common.exception;

public class ScheduleNotFoundException extends RuntimeException {
    public ScheduleNotFoundException(Long id) {
        super("Schedule with ID: " + id + " not found.");
    }
}
