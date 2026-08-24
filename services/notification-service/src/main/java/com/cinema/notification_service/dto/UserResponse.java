package com.cinema.notification_service.dto;

import java.util.UUID;

public record UserResponse(
        UUID id ,
        String email
) {
}
