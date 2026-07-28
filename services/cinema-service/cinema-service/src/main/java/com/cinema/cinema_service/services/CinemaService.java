package com.cinema.cinema_service.services;

import com.cinema.cinema_service.dto.CinemaResponse;
import com.cinema.cinema_service.dto.CreateCinemaRequest;
import com.cinema.cinema_service.dto.UpdateCinemaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CinemaService {

Page<CinemaResponse>  getAllCinemas(Pageable pageable);
 CinemaResponse getCinemaById(UUID id);
 CinemaResponse createCinema(CreateCinemaRequest request);
 CinemaResponse updateCinema(UUID id, UpdateCinemaRequest request);
Page< CinemaResponse> searchCinemaByName(String name , Pageable pageable);
 Page<CinemaResponse> searchCinemaByAddress(String address , Pageable  pageable);
 void deleteCinemaById(UUID id);


}
