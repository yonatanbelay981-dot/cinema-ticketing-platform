package com.cinema.cinema_service.config;

import jakarta.validation.Valid;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${app.kafka.topic.cinema-events}")
    private String cinemaEvent;
    @Value("${app.kafka.topic.hall-availability-events}")
    private String hallEvent;

    @Bean
    public NewTopic kafkaConfigHandler(){
        return TopicBuilder.name(cinemaEvent)
                .partitions(1)
                .replicas(1)
                .configs(Map.of("min.insync.replicas" , "1"))
                .build();


    }
    @Bean
    public NewTopic hallEventsTopic(){
        return TopicBuilder.name(hallEvent)
                .partitions(1)
                .replicas(1)
                .configs(Map.of("min.insync.replicas" , "1"))
                .build();
    }

}
