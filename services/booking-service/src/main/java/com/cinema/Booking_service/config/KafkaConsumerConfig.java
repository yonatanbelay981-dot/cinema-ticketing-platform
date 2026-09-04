package com.cinema.Booking_service.config;

import com.cinema.Booking_service.event.*;
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
    public ConsumerFactory<String , ShowtimePriceResponseEvent>ShowTimeConsumerFactory(){
        JacksonJsonDeserializer<ShowtimePriceResponseEvent> deserializer =  new JacksonJsonDeserializer<>(ShowtimePriceResponseEvent.class);
        deserializer.trustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties("booking-service-group"),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , ShowtimePriceResponseEvent> showTimeKafkaListenerContainerFactory(){
            ConcurrentKafkaListenerContainerFactory<String  , ShowtimePriceResponseEvent> factory =  new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(ShowTimeConsumerFactory());
            factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
            return factory;
    }

    @Bean
    public ConsumerFactory<String , PaymentProcessedEvent> paymentProcessedConsumerFactory(){
        JacksonJsonDeserializer<PaymentProcessedEvent> deserializer = new JacksonJsonDeserializer<>(PaymentProcessedEvent.class);
        deserializer.trustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties("booking-service-group"),
                new StringDeserializer(),
                deserializer
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , PaymentProcessedEvent> paymentProcessedKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String , PaymentProcessedEvent> factory =  new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentProcessedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
    @Bean
    public ConsumerFactory<String , SeatEvent> seatConsumerFactory(){
        JacksonJsonDeserializer<SeatEvent> deserializer = new JacksonJsonDeserializer<>(SeatEvent.class);
        deserializer.trustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties("booking-service-group"),
                new StringDeserializer(),
                deserializer
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , SeatEvent> seatKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String , SeatEvent> factory =  new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(seatConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}
