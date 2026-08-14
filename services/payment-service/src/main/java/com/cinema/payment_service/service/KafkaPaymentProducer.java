package com.cinema.payment_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaPaymentProducer {
    @Value("${app.kafka.topic.payment}")
    private String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaPaymentProducer(
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public <T> CompletableFuture<SendResult<String , Object>> publish(UUID bookingId , T event){
        CompletableFuture<SendResult<String , Object >> future = kafkaTemplate.send(topic , bookingId.toString() , event);
        future.whenComplete((result , ex)->{
            if(ex==null){
                log.info(
                        "Successfully published payment event for booking {} to topic {}",
                        bookingId,
                        topic
                );
            }else {
                log.error(
                        "Failed to publish payment event for booking {}",
                        bookingId,
                        ex
                );

            }
        });
        return future;

    }
}
