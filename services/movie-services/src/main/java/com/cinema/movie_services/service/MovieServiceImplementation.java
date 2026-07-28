package com.cinema.movie_services.service;

import com.cinema.movie_services.dto.request.CreateMovieRequest;
import com.cinema.movie_services.dto.request.UpdateMovieRequest;
import com.cinema.movie_services.dto.response.MovieResponse;
import com.cinema.movie_services.entity.Genre;
import com.cinema.movie_services.entity.Movie;
import com.cinema.movie_services.entity.MovieStatus;
import com.cinema.movie_services.event.MovieEvent;
import com.cinema.movie_services.exception.GenreNotFoundException;
import com.cinema.movie_services.exception.ResourceNotFoundException;
import com.cinema.movie_services.repository.GenreRepository;
import com.cinema.movie_services.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j

public class MovieServiceImplementation implements  MovieService {

    public MovieServiceImplementation(MovieRepository movieRepository, GenreRepository genreRepository, KafkaProducerService kafkaProducerService) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
   public Page<MovieResponse> getAllMovies(Pageable pageable){

       log.info("Fetching all movies");
       Page<Movie> movies = movieRepository.findAll(pageable);
       log.info("movies fetched successfully");
       return movies.map(this::mapToMovieResponse);


   }

    @Override
   public MovieResponse createMovie(CreateMovieRequest request) {
      log.info("Creating movie with title {}"  , request.getTitle());
      Movie movie =  new Movie();
      movie.setTitle(request.getTitle());
      movie.setDescription(request.getDescription());
      movie.setDuration(request.getDuration());
      movie.setLanguage(request.getLanguage());
      movie.setReleaseDate(request.getReleaseDate());
      movie.setAgeRating(request.getAgeRating());
      movie.setPosterUrl(request.getPosterUrl());
      movie.setTrailerUrl(request.getTrailerUrl());
      movie.setStatus(request.getStatus());

        Set<Genre> genres = new HashSet<>(
                genreRepository.findAllById(request.getGenreIds())
        );

        if (genres.size() != request.getGenreIds().size()) {
            throw new GenreNotFoundException("One or more genres were not found");

        }

        movie.setGenres(genres);

      Movie savedMovie =  movieRepository.save(movie);
        CompletableFuture<SendResult<String, MovieEvent>> future = kafkaProducerService.publish(
                new MovieEvent(

                        MovieEvent.EventType.MOVIE_CREATED,
                        savedMovie.getId(),
                        savedMovie.getTitle(),
                        savedMovie.getDuration(),
                        savedMovie.getLanguage()
                )
        );

        future.thenAccept((result)->{

            log.info(
                    "Published CREATED event for movie {} at offset {}",
                    savedMovie.getId(),
                    result.getRecordMetadata().offset()
            );



        }).exceptionally((ex)->{
            log.error(
                    "Failed publishing CREATED event for movie {}",
                    savedMovie.getId(),
                    ex
            );
            return null;
        });

      return  mapToMovieResponse(savedMovie);
   }


    @Override
   public MovieResponse getMovieById(UUID id) {
       log.info("Fetching movie with ID: {}", id);
       Movie movie =  movieRepository.findById(id).orElseThrow(()->{
       log.warn("Movie with ID {} not found", id);
       return new ResourceNotFoundException("Movie with ID " + id + " not found");
    });
    return mapToMovieResponse(movie);
   }

    @Override
    public Page<MovieResponse> searchByTitle(
            String title,
            Pageable pageable
    ){
      log.info("Searching for movies with title containing: {}", title);
       Page<Movie> movies = movieRepository.findByTitleContainingIgnoreCase(title, pageable);
       log.info("Found {} movie with title {}" , movies.getTotalElements(), title);
       return movies.map(this::mapToMovieResponse);

    }

    @Override
   public Page<MovieResponse> searchByLanguage(
            String language,
            Pageable pageable
    ){

       log.info("searching movies with  {}" , language);
       Page<Movie> movies = movieRepository.findByLanguageContainingIgnoreCase(language , pageable);
       log.info("Found {} movies with the language {}", movies.getTotalElements(), language);
       return movies.map(this::mapToMovieResponse);
   }

    @Override
   public  Page<MovieResponse> findByStatus(
            MovieStatus status,
            Pageable pageable
    ){
        log.info("Searching for movies with status: {}", status);

       Page<Movie> movie = movieRepository.findByStatus(status , pageable);
       log.info("Found {} movies with status {} "  ,  movie.getTotalElements() , status );
       return movie.map(this::mapToMovieResponse);
   }

    @Override
   public  Page<MovieResponse> findByGenre(
            String genre,
            Pageable pageable
    ){

        log.info("Searching for movies with genre: {}", genre);
       Page<Movie> movies = movieRepository.findByGenres_NameContainingIgnoreCase(genre, pageable);
       log.info("Found {} movies with genre {}", movies.getTotalElements(), genre);
       return movies.map(this::mapToMovieResponse);
   }

    @Override
    public MovieResponse updateMovie(
            UUID id,
            UpdateMovieRequest request
    ){
        log.info("Updating movie with ID {}", id);
        Movie movie = movieRepository.findById(id).orElseThrow(() -> {
            log.warn("while updating Movie with ID {} not found", id);
            return new ResourceNotFoundException("Movie with ID " + id + " not found");
        });

        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDuration(request.getDuration());
        movie.setLanguage(request.getLanguage());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setAgeRating(request.getAgeRating());

        if(request.getPosterUrl() != null) {
            movie.setPosterUrl(request.getPosterUrl());
        }

        if(request.getTrailerUrl() != null) {
            movie.setTrailerUrl(request.getTrailerUrl());
        }

        movie.setStatus(request.getStatus());
        Set<Genre> genres = new HashSet<>(
                genreRepository.findAllById(request.getGenreIds())
        );

        if (genres.size() != request.getGenreIds().size()) {
            throw new GenreNotFoundException("One or more genres were not found");
        }

        movie.setGenres(genres);

        log.info("Movie {} updated successfully", id);

        CompletableFuture<SendResult<String  , MovieEvent>>  future =  kafkaProducerService.publish(
                new MovieEvent(
                        MovieEvent.EventType.MOVIE_UPDATED,
                        movie.getId(),
                        movie.getTitle(),
                        movie.getDuration(),
                        movie.getLanguage()
                )
        );
        future.thenAccept((result->{
            log.info(
                    "Published UPDATED event for movie {} at offset {}",
                    movie.getId(),
                    result.getRecordMetadata().offset()
            );

        }
        )).exceptionally((ex)->{
            log.error(
                    "Failed publishing UPDATED event for movie {}",
                    movie.getId(),
                    ex
            );
            return null;
        });

        return mapToMovieResponse(movieRepository.save(movie));
    }

    @Override
    public void deleteMovie(UUID id){
        log.info("deleting movie with id {}", id);
        Movie movie =  movieRepository.findById(id).orElseThrow(() -> {
            log.warn("while deleting Movie with ID {} not found", id);
            return new ResourceNotFoundException("Movie with ID " + id + " not found");
        });

        movieRepository.delete(movie);
        log.info("movie with id {} deleted successfully", id);
        CompletableFuture<SendResult<String, MovieEvent>> future = kafkaProducerService.publish(
                new MovieEvent(
                        MovieEvent.EventType.MOVIE_DELETED,
                        movie.getId(),
                        movie.getTitle(),
                        movie.getDuration(),
                        movie.getLanguage()
                )
        );
        future.thenAccept((result)->{
            log.info(
                    "Published DELETED event for movie {} at offset {}",
                    movie.getId(),
                    result.getRecordMetadata().offset()
            );
        }).exceptionally((ex)->{
            log.error(
                    "Failed publishing DELETED event for movie {}",
                    movie.getId(),
                    ex
            );
            return null;
        });
    }

    private MovieResponse mapToMovieResponse(Movie movie) {
    MovieResponse response = new MovieResponse();
    response.setId(movie.getId());
    response.setTitle(movie.getTitle());
    response.setDescription(movie.getDescription());
    response.setDuration(movie.getDuration());
    response.setLanguage(movie.getLanguage());
    response.setReleaseDate(movie.getReleaseDate());
    response.setAgeRating(movie.getAgeRating());
    response.setPosterUrl(movie.getPosterUrl());
    response.setTrailerUrl(movie.getTrailerUrl());
    response.setStatus(movie.getStatus());
    response.setGenres(movie.getGenres().stream().map(Genre::getName).toList());
    return response;



}
}
