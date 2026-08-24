package com.cinema.notification_service.service;

import com.cinema.notification_service.event.EmailNotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationProducer {
    private final RabbitTemplate rabbitTemplate;

    public EmailNotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    @Value("${app.rabbitmq.exchange}")
    private String exchange;
    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    public void sendEmailNotification(EmailNotificationMessage message){
        log.info(
                "Publishing email notification to RabbitMQ. notificationId={}, email={}",
                message.notificationId(),
                message.email()
        );
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                message
        );
        log.info(
                "Email notification published successfully. notificationId={}",
                message.notificationId()
        );
    }

}
