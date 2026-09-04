package com.cinema.Booking_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
public class BookingSeatRequestedEvent {
    public enum EventType {
        BOOKING_SEAT_REQUESTED
    }

    private UUID bookingId;
    private EventType eventType;
    private UUID showTimeId;
    private List<UUID> seatIds;
    private String keycloakUserId;
}
