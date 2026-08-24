package com.cinema.notification_service.dto;

import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID userId

) {
}
