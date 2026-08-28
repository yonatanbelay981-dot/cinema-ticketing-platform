package com.cinema.analytics_service.config;
import com.cinema.common_lib.event.BookingAnalyticsEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public Map<String , Object>consumerProperties(String group_id){
        Map<String , Object>props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG , "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG , group_id);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG , false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG , "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG , StringDeserializer.class);
        return props;

    }

    @Bean
    public ConsumerFactory<String , BookingAnalyticsEvent>bookingAnalyticsEventConsumerFactory(){
        JacksonJsonDeserializer<BookingAnalyticsEvent> deserializer =  new JacksonJsonDeserializer<>(BookingAnalyticsEvent.class);
        deserializer.trustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties("booking-service-group"),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , BookingAnalyticsEvent> bookingAnalyticsEventKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String  , BookingAnalyticsEvent> factory =  new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(bookingAnalyticsEventConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }


}

