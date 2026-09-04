package com.cinema.seat_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;
@Configuration
public class KafkaConfig {
    @Value("${app.kafka.topic.seat-event}")
    private String seatTopicName;
    @Bean
    public NewTopic seatTopic(){
        return TopicBuilder.name(seatTopicName)
                .partitions(1)
                .replicas(1)
                .configs(Map.of("min-insync-replicas", "1"))
                .build();
    }
}