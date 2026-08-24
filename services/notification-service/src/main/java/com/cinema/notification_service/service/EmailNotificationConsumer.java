package com.cinema.notification_service.service;

import com.cinema.notification_service.event.EmailNotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationConsumer {
    private final EmailService emailService;

    public EmailNotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleEmailNotification(EmailNotificationMessage message) {
        log.info(
                "Received email notification. notificationId={}, email={}",
                message.notificationId(),
                message.email()
        );
        emailService.sendEmail(
                message.email(),
                message.subject(),
                message.message()
        );
        log.info(
                "Email processed successfully. notificationId={}",
                message.notificationId()
        );
    }
}
