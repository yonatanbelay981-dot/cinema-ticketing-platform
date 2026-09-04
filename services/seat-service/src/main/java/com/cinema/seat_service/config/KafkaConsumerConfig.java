package com.cinema.seat_service.config;

import com.cinema.seat_service.event.BookEvent;
import com.cinema.seat_service.event.HallEvent;
import com.cinema.seat_service.event.ShowTimeEvent;
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
    public Map<String , Object>consumerProperties(String groupId){
        Map<String , Object> props  = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG , "localhost:9092");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG , false);
        props.put(ConsumerConfig.GROUP_ID_CONFIG ,  groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG ,"earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG , StringDeserializer.class);

        return props;
    }
    @Bean
    public ConsumerFactory<String , HallEvent> hallEventConsumerFactory(){
        JacksonJsonDeserializer<HallEvent>deserializer =  new JacksonJsonDeserializer<>(HallEvent.class);
        deserializer.trustedPackages("*");
        deserializer.setUseTypeHeaders(false);
       return new DefaultKafkaConsumerFactory<>(
                consumerProperties("seat-service-group"),
                new StringDeserializer(),
                deserializer
        );
    }



    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , HallEvent> hallEventConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String , HallEvent> factory =  new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hallEventConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , BookEvent> bookEventConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String , BookEvent> factory =  new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(bookEventConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
    @Bean
    public ConsumerFactory<String , BookEvent> bookEventConsumerFactory(){
        JacksonJsonDeserializer<BookEvent>deserializer =  new JacksonJsonDeserializer<>(BookEvent.class);
        deserializer.trustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties("seat-service-group"),
                new StringDeserializer(),
                deserializer
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , ShowTimeEvent> showTimeEventConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String , ShowTimeEvent> factory =  new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(showTimeEventConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
    @Bean
    public ConsumerFactory<String , ShowTimeEvent> showTimeEventConsumerFactory(){
        JacksonJsonDeserializer<ShowTimeEvent>deserializer =  new JacksonJsonDeserializer<>(ShowTimeEvent.class);
        deserializer.trustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                consumerProperties("seat-service-group"),
                new StringDeserializer(),
                deserializer
        );
    }

}
