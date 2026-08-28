package com.cinema.analytics_service.service;

import com.cinema.analytics_service.dto.MovieAnalyticResponse;
import com.cinema.analytics_service.entity.MovieAnalytics;
import com.cinema.analytics_service.exception.AnalyticMovieNotFoundException;
import com.cinema.analytics_service.repository.MovieAnalyticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@Slf4j
public class MovieAnalyticsServiceImplementation
        implements MovieAnalyticsService {

    private final MovieAnalyticsRepository movieAnalyticsRepository;

    public MovieAnalyticsServiceImplementation(
            MovieAnalyticsRepository movieAnalyticsRepository
    ) {
        this.movieAnalyticsRepository = movieAnalyticsRepository;
    }

    @Override
    public MovieAnalyticResponse getMovieAnalytics(UUID movieId ) {

        log.info(
                "Fetching analytics for movie {}",
                movieId
        );

     MovieAnalytics movieAnalytics = movieAnalyticsRepository.findByMovieId(movieId).orElseThrow(
             ()->{
                 log.info("analytic for movie wad not found with id {}"  , movieId);
                 return  new AnalyticMovieNotFoundException("movie was not found");

             }

     );

        log.info(
                "Fetching analytics for movie {} was successful",
                movieId
        );

     return mapToMovieAnalyticResponse(movieAnalytics);


    }

    @Override
    public Page<MovieAnalyticResponse> getAllMovieAnalytics(Pageable pageable) {

        log.info("Fetching analytics for all movies");
        Page<MovieAnalytics> movieAnalytics = movieAnalyticsRepository.findAll(pageable);
        log.info("Fetching analytics for all movies was successful");
        return movieAnalytics.map(this::mapToMovieAnalyticResponse);

    }

    @Override
    public Page<MovieAnalyticResponse> getPopularMovies(Pageable pageable) {

        log.info("Fetching top 10 popular movies");

        Page<MovieAnalytics> movie = movieAnalyticsRepository.findAllByOrderByTicketsSoldDesc(pageable);
        log.info("Fetching top 10 popular movies was successful");
        return  movie.map(this::mapToMovieAnalyticResponse);

    }

    private MovieAnalyticResponse mapToMovieAnalyticResponse(MovieAnalytics movieAnalytics){
        MovieAnalyticResponse response  =  new MovieAnalyticResponse();
        response.setMovieId(movieAnalytics.getMovieId());
        response.setTicketsSold(movieAnalytics
                .getTicketsSold());
        response.setTotalBookings(movieAnalytics.getTotalBookings());
        response.setTotalRevenue(movieAnalytics.getTotalRevenue());
        return response;
    }
}
