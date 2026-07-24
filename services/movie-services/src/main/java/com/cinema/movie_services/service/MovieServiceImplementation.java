package com.cinema.movie_services.service;

import com.cinema.movie_services.dto.request.CreateMovieRequest;
import com.cinema.movie_services.dto.request.UpdateMovieRequest;
import com.cinema.movie_services.dto.response.MovieResponse;
import com.cinema.movie_services.entity.Genre;
import com.cinema.movie_services.entity.Movie;
import com.cinema.movie_services.entity.MovieStatus;
import com.cinema.movie_services.repository.GenreRepository;
import com.cinema.movie_services.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class MovieServiceImplementation implements  MovieService {

    public MovieServiceImplementation(MovieRepository movieRepository, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
    }

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

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
        movie.setGenres(genres);

      Movie savedMovie =  movieRepository.save(movie);
      return  mapToMovieResponse(savedMovie);
   }

    @Override
   public MovieResponse getMovieById(UUID id) {
       log.info("Fetching movie with ID: {}", id);
       Movie movie =  movieRepository.findById(id).orElseThrow(()->{
       log.warn("Movie with ID {} not found", id);
       return new RuntimeException("Movie not found");
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
        log.info("updating movies...");
        Movie movie = movieRepository.findById(id).orElseThrow(() -> {
            log.warn("while updating Movie with ID {} not found", id);
            return new RuntimeException("Movie not found");
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

        movie.setGenres(genres);

        log.info("update is successful");

        return mapToMovieResponse(movieRepository.save(movie));
    }

    @Override
    public void deleteMovie(UUID id){
        log.info("deleting movie with id {}", id);
        Movie movie =  movieRepository.findById(id).orElseThrow(() -> {
            log.warn("while deleting Movie with ID {} not found", id);
            return new RuntimeException("Movie not found");
        });

        movieRepository.delete(movie);
        log.info("movie with id {} deleted successfully", id);
    }

private MovieResponse mapToMovieResponse(com.cinema.movie_services.entity.Movie movie) {
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
