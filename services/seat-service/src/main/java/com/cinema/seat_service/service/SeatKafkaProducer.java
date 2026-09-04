package com.cinema.seat_service.service;

import com.cinema.seat_service.event.SeatEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class SeatKafkaProducer {
    @Value("${app.kafka.topic.seat-event}")
    private String topic;
    private final KafkaTemplate<String , SeatEvent> kafkaTemplate;

    public SeatKafkaProducer(KafkaTemplate<String, SeatEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public CompletableFuture<SendResult<String , SeatEvent>> publish(SeatEvent event
    ){
        CompletableFuture<SendResult<String , SeatEvent>> future=kafkaTemplate.send(topic  , event.getBookingId().toString() ,  event);

        future.whenComplete((result , ex) -> {
            if(ex != null){
                log.error("Failed to publish seat event for seatId: {} , showTimeId: {} , userId: {} , eventType: {}",
                        event.getBookingId() , event.getShowTimeId() , event.getKeycloakUserId() , event.getEventType() , ex);
            }else{
                log.info("Successfully published seat event for seatId: {} , showTimeId: {} , userId: {} , eventType: {}",
                        event.getBookingId() , event.getShowTimeId() , event.getKeycloakUserId() , event.getEventType());
            }
        });

        return future;
    }
}
