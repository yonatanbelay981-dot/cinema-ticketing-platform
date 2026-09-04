package com.cinema.movie_services.config;

import com.cinema.movie_services.event.MovieEvent;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${app.kafka.topic.movie-events}")
    private String movieEvents;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public NewTopic movieCreatedTopic() {
        return TopicBuilder.name(movieEvents)
                .partitions(3)
                .replicas(1)
                .configs(Map.of("min.insync.replicas", "1"))
                .build();
    }



}






