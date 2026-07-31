package com.cinema.cinema_service.services;
import com.cinema.cinema_service.event.CinemaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaProducerService {

    @Value("${app.kafka.topic.cinema-events}")
    private String topic;

    private final KafkaTemplate<String, CinemaEvent> kafkaTemplate;


    public KafkaProducerService(KafkaTemplate<String, CinemaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public CompletableFuture<SendResult<String, CinemaEvent>> publish(CinemaEvent event) {

        if(event.getCinemaId() == null){
            throw new IllegalArgumentException("Cinema ID cannot be null");
        }

        CompletableFuture<SendResult<String, CinemaEvent>> future =
                kafkaTemplate.send(
                        topic,
                        event.getCinemaId().toString(),
                        event
                );


        future.whenComplete((result, ex) -> {

            if(ex == null){

                log.info(
                        "Published {} event for cinema {} at offset {}",
                        event.getEventType(),
                        event.getCinemaId(),
                        result.getRecordMetadata().offset()
                );

            } else {

                log.error(
                        "Failed publishing {} event for cinema {}",
                        event.getEventType(),
                        event.getCinemaId(),
                        ex
                );

            }

        });

        return future;
    }
}
