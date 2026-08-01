package com.cinema.schedule_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class kafkaConfig {
    @Value("${app.kafka.topic.schedule-events}")
    private String scheduleEvents;

    @Bean
    public NewTopic scheduleEventsTopic() {
        return TopicBuilder.name(scheduleEvents).
                partitions(1)
                .replicas(1)
                .configs(Map.of("min.insync.replicas", "1"))
                .build();
    }

}
