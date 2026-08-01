package com.cinema.schedule_service.repository;

import com.cinema.schedule_service.entity.ScheduleStatus;
import com.cinema.schedule_service.entity.ShowTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ShowTimeRepository extends JpaRepository<ShowTime, UUID> {
   Page<ShowTime> findByMovieId(UUID movieId , Pageable pageable);
   Page<ShowTime> findByHallId(UUID hallId , Pageable pageable);
   Page<ShowTime> findByStatus(ScheduleStatus status , Pageable pageable);
   Page<ShowTime> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime , Pageable pageable);
   boolean existsByHallIdAndStartTimeLessThanAndEndTimeGreaterThan(UUID hallId, LocalDateTime startTime, LocalDateTime endTime);

}
