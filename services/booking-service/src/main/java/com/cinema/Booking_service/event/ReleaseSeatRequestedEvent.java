package com.cinema.Booking_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
public class ReleaseSeatRequestedEvent {
    public enum EventType{
        RELEASE_SEAT_REQUESTED
    }
   private EventType eventType;
    private UUID bookingId;
    private UUID showTimeId;
    private List<UUID> seatIds;
    private String keycloakUserId;

}
