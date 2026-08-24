package com.cinema.notification_service.config;

import com.cinema.notification_service.event.BookingCancelledEvent;
import com.cinema.notification_service.event.BookingConfirmedEvent;
import com.cinema.notification_service.event.PaymentProcessedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Bean
    public Map<String   , Object> consumerProperties(String  groupId){
        Map<String  , Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
    @Bean
    public ConsumerFactory<String , BookingConfirmedEvent>bookingConfirmedEventConsumerFactory(){
        JacksonJsonDeserializer<BookingConfirmedEvent> deserializer = new JacksonJsonDeserializer<>(BookingConfirmedEvent.class);
        deserializer.trustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties("notification-service-group"),
                new StringDeserializer(),
                deserializer
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingConfirmedEvent> bookingConfirmedEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookingConfirmedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(bookingConfirmedEventConsumerFactory());
        return factory;
    }
    @Bean
    public ConsumerFactory<String , PaymentProcessedEvent>paymentProcessedEventConsumerFactory(){
        JacksonJsonDeserializer<PaymentProcessedEvent> deserializer = new JacksonJsonDeserializer<>(PaymentProcessedEvent.class);
        deserializer.trustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties("notification-service-group"),
                new StringDeserializer(),
                deserializer
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentProcessedEvent> paymentProcessedEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentProcessedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentProcessedEventConsumerFactory());
        return factory;
    }
    @Bean
    public ConsumerFactory<String , BookingCancelledEvent>bookingCancelledEventConsumerFactory(){
        JacksonJsonDeserializer<BookingCancelledEvent> deserializer = new JacksonJsonDeserializer<>(BookingCancelledEvent.class);
        deserializer.trustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties("notification-service-group"),
                new StringDeserializer(),
                deserializer
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingCancelledEvent> bookingCancelledEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookingCancelledEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(bookingCancelledEventConsumerFactory());
        return factory;
    }
}
