package com.cinema.schedule_service.service;

import com.cinema.schedule_service.dto.CreateShowtimeRequest;
import com.cinema.schedule_service.dto.ShowtimeResponse;

import com.cinema.schedule_service.dto.UpdateShowtimeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ShowTimeService {

    Page<ShowtimeResponse> getAllSHowTime(Pageable pageable);
    ShowtimeResponse getShowTimeById(UUID id);
    ShowtimeResponse createShowTime(CreateShowtimeRequest request);
    ShowtimeResponse  updateShowTime(UUID id , UpdateShowtimeRequest request);
    void  deleteShowTimeById(UUID id);
    Page<ShowtimeResponse> searchByMovieId(UUID movieId , Pageable pageable);
    Page<ShowtimeResponse> searchByHallId(UUID hallId , Pageable pageable);
   Page<ShowtimeResponse> searchByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime , Pageable pageable);

}
