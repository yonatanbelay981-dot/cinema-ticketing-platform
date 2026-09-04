package com.cinema.seat_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatEvent {



    public enum EventType{
        SEAT_LOCKED,
        SEAT_RELEASED,
        SEAT_BOOKED,
        LOCK_EXPIRED,
        LOCK_FAILED,
    }
    private EventType eventType;
    private UUID bookingId;
    private UUID showTimeId;
    private List<UUID> seatId;
    private String keycloakUserId;
}
