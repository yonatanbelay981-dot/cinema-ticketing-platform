package com.cinema.seat_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatEvent {

    public enum EventType{
        SEAT_LOCKED,
        SEAT_RELEASED,
        SEAT_BOOKED,
        LOCK_EXPIRED
    }
    private EventType eventType;
    private UUID showTimeId;
    private UUID seatId;
    private UUID userId;
}
