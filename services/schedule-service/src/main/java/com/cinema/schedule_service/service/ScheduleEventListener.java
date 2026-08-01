package com.cinema.schedule_service.service;

import com.cinema.schedule_service.entity.HallCache;
import com.cinema.schedule_service.entity.MovieCache;
import com.cinema.schedule_service.event.HallEvents;
import com.cinema.schedule_service.event.MovieEvent;
import com.cinema.schedule_service.repository.HallCacheRepository;
import com.cinema.schedule_service.repository.MovieCacheRepository;
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

    public ScheduleEventListener(MovieCacheRepository movieCacheRepository, HallCacheRepository hallCacheRepository) {
        this.movieCacheRepository = movieCacheRepository;
        this.hallCacheRepository = hallCacheRepository;
    }

    @KafkaListener(
            topics = "movie-events",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listenMovieEvents(MovieEvent event, Acknowledgment ack) {

        try {

            switch (event.getEventType() ) {

                case MOVIE_CREATED:
                case MOVIE_UPDATED:
                    upsertMovie(event);
                    break;

                case MOVIE_DELETED:
                    deleteMovie(event.getMovieId());
                    break;

                default:
                    log.warn("Unknown movie event type: {}", event.getEventType());
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
            topics = "hall-events",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listenHallEvent(HallEvents events ,  Acknowledgment ack){
        try {
            switch (events.getEventType()){
                case HALL_CREATED:
                case HALL_UPDATED:
                    upsertHall(events);
                    break;
                case HALL_DELETED:
                    deleteHall(events.getHallId());
                    break;
                default:
                    log.warn("Unknown hall event type: {}", events.getEventType());
            }
            ack.acknowledge();

        } catch (Exception e) {
            log.error(
                    "Failed processing hall event {}",
                    events.getHallId(),
                    e
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
    private void upsertHall(HallEvents events) {

        HallCache hall = hallCacheRepository
                .findById(events.getHallId())
                .orElseGet(HallCache::new);

        hall.setHallId(events.getHallId());
        hall.setName(events.getName());
        hall.setCapacity(events.getCapacity());

        hallCacheRepository.save(hall);

        log.info(
                "Hall cache synchronized successfully. Event={}, Hall={}",
                events.getEventType(),
                events.getHallId()
        );
    }

    private void deleteHall(UUID hallId) {
        if (hallCacheRepository.existsById(hallId)) {
            hallCacheRepository.deleteById(hallId);
            log.info("Hall {} removed from local cache", hallId);
        } else {
            log.warn("Hall {} not found in local cache", hallId);
        }
    }
}