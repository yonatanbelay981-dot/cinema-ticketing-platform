package com.cinema.schedule_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "show_times",
        indexes = {
                @Index(name = "idx_show_time_movie_id", columnList = "movieId"),
                @Index(name = "idx_show_time_hall_id", columnList = "hallId"),
                @Index(name = "idx_show_time_start_time", columnList = "startTime")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID movieId;

    @Column(nullable = false)
    private UUID hallId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
