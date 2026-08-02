package com.cinema.schedule_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeEvent {
    public enum EventType {
        SHOWTIME_CREATED,
        SHOWTIME_UPDATED,
        SHOWTIME_DELETED
    }

    private EventType eventType;
    private UUID showtimeId;
    private UUID movieId;
    private UUID hallId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
