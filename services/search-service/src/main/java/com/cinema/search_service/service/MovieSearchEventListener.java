package com.cinema.search_service.service;

import com.cinema.common_lib.event.MovieSearchEvent;
import com.cinema.search_service.document.MovieDocument;
import com.cinema.search_service.entity.MovieStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MovieSearchEventListener {

    private final MovieSearchService movieSearchService;

    public MovieSearchEventListener(MovieSearchService movieSearchService) {
        this.movieSearchService = movieSearchService;
    }

    @KafkaListener(
            topics = "movie-search-event",
            groupId = "search-service-group",
            containerFactory = "movieSearchEventConcurrentKafkaListenerContainerFactory"
    )
    public void listenMovieSearchEvent(MovieSearchEvent event) {

        log.info(
                "Received {} event for movie {}",
                event.getEventType(),
                event.getMovieId()
        );

        switch (event.getEventType()) {

            case MOVIE_CREATED:
                handleCreateMovieForElasticSearch(event);
                break;

            case MOVIE_UPDATED:
                handleUpdateMovieForElasticSearch(event);
                break;

            case MOVIE_DELETED:
                handleDeleteMovieForElasticSearch(event);
                break;

            default:
                log.warn(
                        "Unknown event type received: {}",
                        event.getEventType()
                );
        }
    }

    private void handleCreateMovieForElasticSearch(MovieSearchEvent event) {

        log.info(
                "Creating movie document in Elasticsearch for movie {}",
                event.getMovieId()
        );

        MovieDocument movieDocument = mapToMovieDocument(event);

        movieSearchService.createMovieDocument(movieDocument);

        log.info(
                "Successfully created movie document in Elasticsearch for movie {}",
                event.getMovieId()
        );
    }

    private void handleUpdateMovieForElasticSearch(MovieSearchEvent event) {

        log.info(
                "Updating movie document in Elasticsearch for movie {}",
                event.getMovieId()
        );

        MovieDocument movieDocument = mapToMovieDocument(event);

        movieSearchService.updateMovie(
                movieDocument,
                event.getMovieId()
        );

        log.info(
                "Successfully updated movie document in Elasticsearch for movie {}",
                event.getMovieId()
        );
    }

    private void handleDeleteMovieForElasticSearch(MovieSearchEvent event) {

        log.info(
                "Deleting movie document from Elasticsearch for movie {}",
                event.getMovieId()
        );

        movieSearchService.deleteMovieDocument(
                event.getMovieId()
        );

        log.info(
                "Successfully deleted movie document from Elasticsearch for movie {}",
                event.getMovieId()
        );
    }

    private MovieDocument mapToMovieDocument(MovieSearchEvent event) {

        MovieDocument movieDocument = new MovieDocument();

        movieDocument.setId(event.getMovieId());
        movieDocument.setTitle(event.getTitle());
        movieDocument.setDescription(event.getDescription());
        movieDocument.setDuration(event.getDuration());
        movieDocument.setLanguage(event.getLanguage());
        movieDocument.setReleaseDate(event.getReleaseDate());
        movieDocument.setAgeRating(event.getAgeRating());
        movieDocument.setPosterUrl(event.getPosterUrl());
        movieDocument.setTrailerUrl(event.getTrailerUrl());

        movieDocument.setStatus(
                MovieStatus.valueOf(event.getStatus())
        );

        movieDocument.setGenres(event.getGenres());

        movieDocument.setCreatedAt(event.getCreatedAt());
        movieDocument.setUpdatedAt(event.getUpdatedAt());

        return movieDocument;
    }
}