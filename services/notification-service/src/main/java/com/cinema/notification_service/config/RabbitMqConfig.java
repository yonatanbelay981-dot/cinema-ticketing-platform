package com.cinema.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.email-queue}")
    private String emailQueue;

    @Value("${app.rabbitmq.email-routing-key}")
    private String routingKey;

    @Bean
    public DirectExchange notificationExchange(){
        return new DirectExchange(exchange);
    }
    @Bean
    public Queue emailNotificationQueue(){
        return QueueBuilder
                .durable(emailQueue)
                .build();
    }
    @Bean
    public DirectExchange notificationExchangeDlx(){
        return new DirectExchange(exchange + ".dlx");
    }
    @Bean
    public Queue emailNotificationQueueDlq(){
        return  QueueBuilder.durable(
                emailQueue + ".dlq"
        ).build();
    }

    @Bean
    public Binding emailNotificationBindingDlq(Queue emailNotificationQueueDlq  , DirectExchange notificationExchangeDlx ){
        return BindingBuilder.bind(emailNotificationQueueDlq)
                .to(notificationExchangeDlx)
                .with(routingKey +   ".dead");


    }

    @Bean
    public Binding emailNotificationBinding(Queue emailNoficationQueue , DirectExchange notificationExchange){
        return BindingBuilder
                .bind(emailNoficationQueue)
                .to(notificationExchange)
                .with(routingKey);
    }
}
