package com.cinema.concession_service.service;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaConcessionProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.food-order-events}")
    private String foodOrderEventsTopic;

    public KafkaConcessionProducer(
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, Object>> publish(
            UUID foodOrderId,
            Object event
    ) {

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(
                        foodOrderEventsTopic,
                        foodOrderId.toString(),
                        event
                );

        future.whenComplete((result, ex) -> {

            if (ex == null) {

                log.info(
                        "Successfully published food order event. " +
                                "foodOrderId={}, topic={}",
                        foodOrderId,
                        foodOrderEventsTopic
                );

            } else {

                log.error(
                        "Failed to publish food order event. " +
                                "foodOrderId={}",
                        foodOrderId,
                        ex
                );
            }
        });

        return future;
    }
}
