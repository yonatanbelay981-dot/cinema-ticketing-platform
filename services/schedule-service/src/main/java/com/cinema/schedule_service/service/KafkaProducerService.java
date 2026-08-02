package com.cinema.schedule_service.service;

import com.cinema.schedule_service.event.ShowtimeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
@Service
@Slf4j
public class KafkaProducerService {
    @Value("${app.kafka.topic.schedule-events}")
    private String topic;

    private final KafkaTemplate<String  , ShowtimeEvent> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, ShowtimeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String , ShowtimeEvent>>publish(ShowtimeEvent event){
        CompletableFuture<SendResult<String , ShowtimeEvent>> future = kafkaTemplate.send(topic  , event.getShowtimeId().toString() , event);
        future.whenComplete((result  , ex)->{
            if(ex==null){
                log.info("published {} event for {} showtime at {} offset" , event.getEventType() , event.getShowtimeId() , result.getRecordMetadata().offset());
            }
            else{
                log.warn("failed publishing {} event for {} showtime" , event.getEventType() , event.getShowtimeId() , ex);

            }
        });
        return future;
    }

}
