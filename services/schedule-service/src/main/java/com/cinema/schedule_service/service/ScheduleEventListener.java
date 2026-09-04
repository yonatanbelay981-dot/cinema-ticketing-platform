package com.cinema.schedule_service.service;

import com.cinema.schedule_service.entity.HallCache;
import com.cinema.schedule_service.entity.MovieCache;
import com.cinema.schedule_service.entity.ShowTime;
import com.cinema.schedule_service.event.HallEvents;
import com.cinema.schedule_service.event.MovieEvent;
import com.cinema.schedule_service.event.ShowtimePriceRequestedEvent;
import com.cinema.schedule_service.event.ShowtimePriceResponseEvent;
import com.cinema.schedule_service.repository.HallCacheRepository;
import com.cinema.schedule_service.repository.MovieCacheRepository;
import com.cinema.schedule_service.repository.ShowTimeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ScheduleEventListener {

    private final MovieCacheRepository movieCacheRepository;
    private final HallCacheRepository hallCacheRepository;
    private final ShowTimeRepository showtimeRepository;
    private final ShowtimePriceKafkaProducer showtimePriceKafkaProducer;

    public ScheduleEventListener(
            MovieCacheRepository movieCacheRepository,
            HallCacheRepository hallCacheRepository,
            ShowTimeRepository showtimeRepository,
            ShowtimePriceKafkaProducer showtimePriceKafkaProducer
    ) {
        this.movieCacheRepository = movieCacheRepository;
        this.hallCacheRepository = hallCacheRepository;
        this.showtimeRepository = showtimeRepository;
        this.showtimePriceKafkaProducer = showtimePriceKafkaProducer;
    }


    @KafkaListener(
            topics = "movie-events",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "movieKafkaListenerContainerFactory"
    )
    public void listenMovieEvents(
            MovieEvent event,
            Acknowledgment ack
    ) {

        try {

            switch (event.getEventType()) {

                case MOVIE_CREATED:
                case MOVIE_UPDATED:
                    upsertMovie(event);
                    break;

                case MOVIE_DELETED:
                    deleteMovie(event.getMovieId());
                    break;

                default:
                    log.warn(
                            "Unknown movie event type: {}",
                            event.getEventType()
                    );
            }

            ack.acknowledge();

        } catch (Exception ex) {

            log.error(
                    "Failed processing movie event {}",
                    event.getMovieId(),
                    ex
            );
        }
    }


    @KafkaListener(
            topics = "hall-availability-events",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "hallKafkaListenerContainerFactory"
    )
    public void listenHallEvent(
            HallEvents event,
            Acknowledgment ack
    ) {

        try {

            switch (event.getEventType()) {

                case HALL_CREATED:
                case HALL_UPDATED:
                    upsertHall(event);
                    break;

                case HALL_DELETED:
                    deleteHall(event.getHallId());
                    break;

                default:
                    log.warn(
                            "Unknown hall event type: {}",
                            event.getEventType()
                    );
            }

            ack.acknowledge();

        } catch (Exception ex) {

            log.error(
                    "Failed processing hall event {}",
                    event.getHallId(),
                    ex
            );
        }
    }

    private void upsertMovie(MovieEvent event) {

        MovieCache movie =
                movieCacheRepository
                        .findById(event.getMovieId())
                        .orElse(new MovieCache());

        movie.setMovieId(event.getMovieId());
        movie.setTitle(event.getTitle());
        movie.setDuration(event.getDuration());

        movieCacheRepository.save(movie);

        log.info(
                "Movie cache synchronized successfully. Event={}, Movie={}",
                event.getEventType(),
                event.getMovieId()
        );
    }

    private void deleteMovie(UUID movieId) {

        if (movieCacheRepository.existsById(movieId)) {

            movieCacheRepository.deleteById(movieId);

            log.info(
                    "Movie {} removed from local cache",
                    movieId
            );

        } else {

            log.warn(
                    "Movie {} not found in local cache",
                    movieId
            );
        }
    }


    private void upsertHall(HallEvents event) {

        HallCache hall =
                hallCacheRepository
                        .findById(event.getHallId())
                        .orElseGet(HallCache::new);

        hall.setHallId(event.getHallId());
        hall.setName(event.getName());
        hall.setCapacity(event.getCapacity());

        hallCacheRepository.save(hall);

        log.info(
                "Hall cache synchronized successfully. Event={}, Hall={}",
                event.getEventType(),
                event.getHallId()
        );
    }

    private void deleteHall(UUID hallId) {

        if (hallCacheRepository.existsById(hallId)) {

            hallCacheRepository.deleteById(hallId);

            log.info(
                    "Hall {} removed from local cache",
                    hallId
            );

        } else {

            log.warn(
                    "Hall {} not found in local cache",
                    hallId
            );
        }
    }



    @KafkaListener(
            topics = "showtime-price-requests",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "showtimePriceKafkaListenerContainerFactory"
    )
    public void listenShowtimePriceRequest(
            ShowtimePriceRequestedEvent event,
            Acknowledgment ack
    ) {

        try {



            log.info(
                    "SHOWTIME PRICE REQUEST RECEIVED"
            );

            log.info(
                    "BookingId={}",
                    event.getBookingId()
            );

            log.info(
                    "ShowtimeId={}",
                    event.getShowtimeId()
            );





            ShowTime showtime =
                    showtimeRepository
                            .findById(event.getShowtimeId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Showtime not found: "
                                                    + event.getShowtimeId()
                                    )
                            );

            log.info(
                    "Showtime found. id={}, basePrice={}",
                    showtime.getId(),
                    showtime.getBasePrice()
            );



            ShowtimePriceResponseEvent response =
                    new ShowtimePriceResponseEvent(
                            event.getBookingId(),
                            showtime.getId(),
                            showtime.getMovieId(),
                            showtime.getBasePrice()
                    );

            log.info(
                    "Created price response. bookingId={}, price={}",
                    response.getBookingId(),
                    response.getBasePrice()
            );



            showtimePriceKafkaProducer
                    .publish(
                            event.getBookingId(),
                            response
                    )
                    .whenComplete((result, ex) -> {

                        if (ex == null) {


                            log.info(
                                    "SHOWTIME PRICE RESPONSE SENT SUCCESSFULLY"
                            );

                            log.info(
                                    "BookingId={}",
                                    event.getBookingId()
                            );

                            log.info(
                                    "Price={}",
                                    showtime.getBasePrice()
                            );

                            log.info(
                                    "Topic=showtime-price-responses"
                            );

                            log.info(
                                    "Partition={}",
                                    result.getRecordMetadata().partition()
                            );

                            log.info(
                                    "Offset={}",
                                    result.getRecordMetadata().offset()
                            );



                        } else {



                            log.error(
                                    "FAILED TO SEND SHOWTIME PRICE RESPONSE"
                            );

                            log.error(
                                    "BookingId={}",
                                    event.getBookingId()
                            );


                        }
                    });

            ack.acknowledge();

        } catch (Exception ex) {

            log.error(
                    "Failed processing showtime price request. BookingId={}",
                    event.getBookingId(),
                    ex
            );
        }
    }
}