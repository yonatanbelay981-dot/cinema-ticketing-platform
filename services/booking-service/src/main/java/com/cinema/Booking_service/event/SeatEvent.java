package com.cinema.Booking_service.event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatEvent {
    public enum EventType {
        SEAT_LOCKED,
        LOCK_FAILED,
        LOCK_EXPIRED,
        SEAT_BOOKED,
        SEAT_RELEASED
    }
    private EventType eventType;
    private UUID showTimeId;
    private UUID bookingId;
    private List<UUID> seatIds;
    private String keycloakUserId;

}
