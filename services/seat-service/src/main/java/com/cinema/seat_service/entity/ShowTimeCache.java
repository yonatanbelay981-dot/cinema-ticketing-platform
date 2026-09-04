package com.cinema.seat_service.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ShowTimeCache {
    private String eventType;
    @Id
    private UUID showtimeId;
    private UUID movieId;
    private UUID hallId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
