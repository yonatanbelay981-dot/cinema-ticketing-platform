package com.cinema.movie_services.service;

import com.cinema.common_lib.event.MovieSearchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MovieSearchKafkaProducer {
    @Value("${app.kafka.topic.movie-search-event}")
    private String topic;

    private final KafkaTemplate<String , MovieSearchEvent> kafkaTemplate;


    public MovieSearchKafkaProducer(KafkaTemplate<String , MovieSearchEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;

    }
    public CompletableFuture<SendResult<String , MovieSearchEvent>>publishSearchEvent(MovieSearchEvent event){
        CompletableFuture<SendResult<String , MovieSearchEvent>> future = kafkaTemplate.send(topic , event.getMovieId().toString() ,event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info( "Published {} search event for movie {} at offset {}",
                        event.getEventType(),
                        event.getMovieId(),
                        result.getRecordMetadata().offset() );
            } else {
                log.error( "Failed publishing {} search event for movie {}",
                        event.getEventType(),
                        event.getMovieId(), ex );
            }
        }
        );
        return future;
    }
    }

