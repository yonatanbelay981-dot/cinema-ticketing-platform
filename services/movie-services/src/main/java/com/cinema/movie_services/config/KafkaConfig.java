package com.cinema.movie_services.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${app.kafka.topic.movie-events}")
    private String movieEvents;

    @Bean
    public NewTopic movieCreatedTopic() {
        return TopicBuilder.name(movieEvents)
                .partitions(3)
                .replicas(1)
                .configs(Map.of("min.insync.replicas", "1"))
                .build();
    }





}
