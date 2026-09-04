package com.cinema.schedule_service.config;
import com.cinema.schedule_service.event.HallEvents;
import com.cinema.schedule_service.event.MovieEvent;
import com.cinema.schedule_service.event.ShowtimePriceRequestedEvent;
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
public class KafkaConfigCreation {



        private Map<String, Object> consumerProperties(String groupId) {

            Map<String, Object> props = new HashMap<>();

            props.put(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                    "localhost:9092"
            );

            props.put(
                    ConsumerConfig.GROUP_ID_CONFIG,
                    groupId
            );

            props.put(
                    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                    false
            );

            props.put(
                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                    "earliest"
            );

            props.put(
                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    StringDeserializer.class
            );

            return props;
        }

        // =========================
        // MOVIE EVENT CONSUMER
        // =========================

        @Bean
        public ConsumerFactory<String, MovieEvent> movieEventConsumerFactory() {

            JacksonJsonDeserializer<MovieEvent> deserializer =
                    new JacksonJsonDeserializer<>(MovieEvent.class);

            deserializer.addTrustedPackages(
                    "com.cinema.schedule_service.event"
            );

            // Don't depend on Java class information
            // stored in Kafka headers.
            deserializer.setUseTypeHeaders(false);

            return new DefaultKafkaConsumerFactory<>(
                    consumerProperties("schedule-movie-group"),
                    new StringDeserializer(),
                    deserializer
            );
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, MovieEvent>
        movieKafkaListenerContainerFactory() {

            ConcurrentKafkaListenerContainerFactory<String, MovieEvent> factory =
                    new ConcurrentKafkaListenerContainerFactory<>();

            factory.setConsumerFactory(movieEventConsumerFactory());

            factory.getContainerProperties().setAckMode(
                    ContainerProperties.AckMode.MANUAL_IMMEDIATE
            );

            return factory;
        }

        // =========================
        // HALL EVENT CONSUMER
        // =========================

        @Bean
        public ConsumerFactory<String, HallEvents> hallEventConsumerFactory() {

            JacksonJsonDeserializer<HallEvents> deserializer =
                    new JacksonJsonDeserializer<>(HallEvents.class);

            deserializer.addTrustedPackages(
                    "com.cinema.schedule_service.event"
            );

            deserializer.setUseTypeHeaders(false);

            return new DefaultKafkaConsumerFactory<>(
                    consumerProperties("schedule-hall-group"),
                    new StringDeserializer(),
                    deserializer
            );
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, HallEvents>
        hallKafkaListenerContainerFactory() {

            ConcurrentKafkaListenerContainerFactory<String, HallEvents> factory =
                    new ConcurrentKafkaListenerContainerFactory<>();

            factory.setConsumerFactory(hallEventConsumerFactory());

            factory.getContainerProperties().setAckMode(
                    ContainerProperties.AckMode.MANUAL_IMMEDIATE
            );

            return factory;
        }
    @Bean
    public ConsumerFactory<String, ShowtimePriceRequestedEvent>
    showtimePriceRequestedEventConsumerFactory() {

        JacksonJsonDeserializer<ShowtimePriceRequestedEvent> deserializer =
                new JacksonJsonDeserializer<>(ShowtimePriceRequestedEvent.class);

        deserializer.addTrustedPackages(
                "com.cinema.schedule_service.event"
        );

        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                consumerProperties("schedule-showtime-price-group"),
                new StringDeserializer(),
                deserializer
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ShowtimePriceRequestedEvent>
    showtimePriceKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, ShowtimePriceRequestedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                showtimePriceRequestedEventConsumerFactory()
        );

        factory.getContainerProperties().setAckMode(
                ContainerProperties.AckMode.MANUAL_IMMEDIATE
        );

        return factory;
    }
    }

