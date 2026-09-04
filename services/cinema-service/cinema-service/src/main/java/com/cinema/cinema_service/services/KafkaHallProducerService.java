package com.cinema.cinema_service.services;

import com.cinema.cinema_service.event.HallEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
@Service
@Slf4j
public class KafkaHallProducerService {
    @Value("${app.kafka.topic.hall-availability-events}")
    private String topic;
    private final KafkaTemplate<String, HallEvent> kafkaTemplate;

    public KafkaHallProducerService(KafkaTemplate<String, HallEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, HallEvent>> publish(HallEvent event) {

        if (event.getHallId() == null) {
            throw new IllegalArgumentException("Hall ID cannot be null");
        }

        CompletableFuture<SendResult<String, HallEvent>> future =
                kafkaTemplate.send(
                        topic,
                        event.getHallId().toString(),
                        event
                );

        future.whenComplete((result, ex) -> {

            if (ex == null) {
                log.info(
                        "Published {} event for hall {} at offset {}",
                        event.getEventType(),
                        event.getHallId(),
                        result.getRecordMetadata().offset()
                );
            } else {
                log.error(
                        "Failed publishing {} event for hall {}",
                        event.getEventType(),
                        event.getHallId(),
                        ex
                );
            }
        });

        return future;
    }

}
