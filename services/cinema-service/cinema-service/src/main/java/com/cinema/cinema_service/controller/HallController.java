package com.cinema.cinema_service.controller;

import com.cinema.cinema_service.dto.*;
import com.cinema.cinema_service.services.HallServices;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/halls")
public class HallController {
    private final HallServices hallServices;

    public HallController(HallServices hallServices) {
        this.hallServices = hallServices;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HallResponse>>>getAllHalls(@PageableDefault(page = 0 , size = 10  , sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        Page<HallResponse> halls = hallServices.getAllHalls(pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "halls successfully fetched",
                        halls
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HallResponse>> createHall(@Valid @RequestBody CreateHallRequest request){
        HallResponse hall   = hallServices.createHall(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "halls successfully created",
                        hall
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HallResponse>> updateHall(@PathVariable("id") UUID id, @Valid @RequestBody UpdateHallRequest request){
        HallResponse hall   = hallServices.updateHall(id ,  request);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "halls successfully updated",
                        hall
                )
        );


    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<HallResponse>>> searchByName(@RequestParam(value = "name" , required = false) String name , @PageableDefault(page = 0 , size = 10  , sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        Page<HallResponse> hall = hallServices.searchByName(name , pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "hall is searched successfully",
                        hall
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HallResponse>> getHallById(@PathVariable("id") UUID id){
        HallResponse response = hallServices.getHallById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "fetching hall with id was successful",
                        response
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHallById(@PathVariable("id") UUID id){
        hallServices.deleteHallById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "hall  deleted successfully",
                        null
                )
        );
    }
}
