package com.kadir.smartirrigation.infrastructure.repository.schedule;

import com.kadir.smartirrigation.domain.model.schedule.IrrigationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface IrrigationScheduleRepository extends JpaRepository<IrrigationSchedule, Long> {
    @Query("SELECT s FROM IrrigationSchedule s WHERE s.active = true AND (" +
            "((s.repeatDaily = true OR :day MEMBER OF s.days) AND s.time = :time)) " +
            "OR (s.specificDate = :date AND s.time = :time)")
    List<IrrigationSchedule> findScheduleForTime(LocalTime time, DayOfWeek day, LocalDate date);
}
