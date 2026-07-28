package com.cinema.movie_services.service;

import com.cinema.movie_services.event.MovieEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaProducerService {

    @Value("${app.kafka.topic.movie-events}")
    private String topic;

    private final KafkaTemplate<String, MovieEvent> kafkaTemplate;


    public KafkaProducerService(KafkaTemplate<String, MovieEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public CompletableFuture<SendResult<String, MovieEvent>> publish(MovieEvent event) {

        if(event.getMovieId() == null){
            throw new IllegalArgumentException("Movie ID cannot be null");
        }

        CompletableFuture<SendResult<String, MovieEvent>> future =
                kafkaTemplate.send(
                        topic,
                        event.getMovieId().toString(),
                        event
                );


        future.whenComplete((result, ex) -> {

            if(ex == null){

                log.info(
                        "Published {} event for movie {} at offset {}",
                        event.getEventType(),
                        event.getMovieId(),
                        result.getRecordMetadata().offset()
                );

            } else {

                log.error(
                        "Failed publishing {} event for movie {}",
                        event.getEventType(),
                        event.getMovieId(),
                        ex
                );

            }

        });

        return future;
    }
}
