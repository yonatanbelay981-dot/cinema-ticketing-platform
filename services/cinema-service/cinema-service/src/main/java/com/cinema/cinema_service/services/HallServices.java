package com.cinema.cinema_service.services;

import com.cinema.cinema_service.dto.CreateHallRequest;
import com.cinema.cinema_service.dto.HallResponse;
import com.cinema.cinema_service.dto.UpdateHallRequest;
import com.cinema.cinema_service.entity.Hall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HallServices {
    Page<HallResponse> getAllHalls(Pageable pageable);
    HallResponse createHall(CreateHallRequest request);
    HallResponse updateHall(UUID id ,  UpdateHallRequest request);
    Page<HallResponse> searchByName(String name  , Pageable pageable);
    HallResponse getHallById(UUID id);
    void deleteHallById(UUID id);


}
