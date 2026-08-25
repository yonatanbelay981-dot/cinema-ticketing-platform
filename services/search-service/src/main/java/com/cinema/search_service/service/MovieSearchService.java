package com.cinema.search_service.service;

import com.cinema.search_service.document.MovieDocument;
import com.cinema.search_service.repository.MovieSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MovieSearchService {

    private final MovieSearchRepository movieSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public MovieSearchService(MovieSearchRepository movieSearchRepository, ElasticsearchOperations elasticsearchOperations) {
        this.movieSearchRepository = movieSearchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public MovieDocument createMovieDocument(MovieDocument movie) {

        log.info(
                "Saving movie in Elasticsearch. movieId={}",
                movie.getId()
        );

        MovieDocument savedMovie =
                movieSearchRepository.save(movie);

        log.info(
                "Movie successfully saved in Elasticsearch. movieId={}",
                savedMovie.getId()
        );

        return savedMovie;
    }

    public MovieDocument updateMovie(
            MovieDocument movie,
            UUID id
    ) {

        log.info(
                "Updating movie document. movieId={}",
                id
        );

        MovieDocument movieDocument =
                movieSearchRepository.findById(id)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Movie document not found. movieId={}",
                                    id
                            );

                            return new RuntimeException(
                                    "Movie was not found with id: " + id
                            );
                        });

        movieDocument.setTitle(movie.getTitle());
        movieDocument.setDescription(movie.getDescription());
        movieDocument.setDuration(movie.getDuration());
        movieDocument.setLanguage(movie.getLanguage());
        movieDocument.setReleaseDate(movie.getReleaseDate());
        movieDocument.setAgeRating(movie.getAgeRating());
        movieDocument.setPosterUrl(movie.getPosterUrl());
        movieDocument.setTrailerUrl(movie.getTrailerUrl());
        movieDocument.setStatus(movie.getStatus());
        movieDocument.setGenres(movie.getGenres());
        movieDocument.setUpdatedAt(movie.getUpdatedAt());

        MovieDocument savedMovieDocument =
                movieSearchRepository.save(movieDocument);

        log.info(
                "Movie document successfully updated in Elasticsearch. movieId={}",
                id
        );

        return savedMovieDocument;
    }

    public void deleteMovieDocument(UUID id) {

        log.info(
                "Deleting movie document from Elasticsearch. movieId={}",
                id
        );

        if (!movieSearchRepository.existsById(id)) {

            log.warn(
                    "Movie document not found. movieId={}",
                    id
            );

            return;
        }

        movieSearchRepository.deleteById(id);

        log.info(
                "Movie document successfully deleted from Elasticsearch. movieId={}",
                id
        );
    }
    public Page<MovieDocument> searchMovies(String query , Pageable pageable){
        log.info(
                "Searching movies with query: '{}'",
                query
        );
        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(q->q
                        .multiMatch(m->m
                                .query(query)
                                .fields("title",
                                            "genre",
                                                    "language",
                                                     "description"))).withPageable(pageable).build();
     SearchHits<MovieDocument> searchHit = elasticsearchOperations.search(
             searchQuery ,
             MovieDocument.class);

        List<MovieDocument> movies = searchHit.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();

        log.info(
                "Found {} movies for query '{}'",
                searchHit.getTotalHits(),
                query
        );

        return new PageImpl<>(
                movies,
                pageable,
                searchHit.getTotalHits()
        );

    }
}