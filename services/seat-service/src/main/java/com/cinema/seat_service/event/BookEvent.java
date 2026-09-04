package com.cinema.seat_service.event;

import com.cinema.seat_service.entity.ReservationStatus;
import lombok.*;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor

public class BookEvent {
    public enum EventType {
        LOCK_SEATS_REQUESTED,
        RELEASE_SEAT_REQUESTED,
        BOOKING_SEAT_REQUESTED
    }

    public EventType eventType;
    private UUID bookingId;
    private UUID showTimeId;
    private List<UUID> seatIds;
    private String keycloakUserId;

    public BookEvent(EventType eventType, UUID bookingId, UUID showTimeId, List<UUID> seatIds, String keycloakUserId) {
        this.eventType = eventType;
        this.bookingId = bookingId;
        this.showTimeId = showTimeId;
        this.seatIds = seatIds;
        this.keycloakUserId = keycloakUserId;
    }
}

