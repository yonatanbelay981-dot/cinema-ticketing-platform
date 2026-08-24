package com.cinema.notification_service.event;

import java.util.UUID;

public record EmailNotificationMessage(
    UUID notificationId,
    UUID userId,
    String email,
    String subject,
    String message

) {
}
