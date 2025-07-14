package com.kadir.smartirrigation.domain.event;

import java.time.LocalTime;

public record ScheduledIrrigationEvent(int duration, LocalTime time) {
}
