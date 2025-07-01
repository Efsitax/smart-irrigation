package com.kadir.smartirrigation.schedule.exception;

public class ScheduleNotFoundException extends RuntimeException {
    public ScheduleNotFoundException(Long id) {
        super("Schedule with ID: " + id + " not found.");
    }
}
