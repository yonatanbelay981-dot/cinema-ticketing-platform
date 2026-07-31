package com.cinema.schedule_service.dto;

import com.cinema.schedule_service.entity.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ShowtimeResponse {
  private UUID id;
  private UUID movieId;
  private UUID hallId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private BigDecimal basePrice;
  private ScheduleStatus status;
}
