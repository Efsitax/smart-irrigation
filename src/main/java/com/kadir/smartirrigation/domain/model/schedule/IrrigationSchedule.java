package com.kadir.smartirrigation.domain.model.schedule;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IrrigationSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> days;

    @Column(name = "duration_in_seconds")
    private int durationInSeconds;

    @Column(name = "repeat_daily")
    private boolean repeatDaily;

    @Column(name = "specific_date")
    private LocalDate specificDate;

    private LocalTime time;
    private boolean active;
}
