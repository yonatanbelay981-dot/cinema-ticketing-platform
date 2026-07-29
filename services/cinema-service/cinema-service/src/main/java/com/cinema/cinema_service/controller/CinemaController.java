package com.cinema.cinema_service.controller;

import com.cinema.cinema_service.dto.ApiResponse;
import com.cinema.cinema_service.dto.CinemaResponse;
import com.cinema.cinema_service.dto.CreateCinemaRequest;
import com.cinema.cinema_service.dto.UpdateCinemaRequest;
import com.cinema.cinema_service.services.CinemaService;
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
@RequestMapping("/api/cinemas")
public class CinemaController {
    private final CinemaService cinemaService;

    public CinemaController( CinemaService cinemaService) {
        this.cinemaService = cinemaService;

    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CinemaResponse>>> getAllCinema( @PageableDefault(page = 0 , size = 10  , sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        Page<CinemaResponse> cinemaResponse = cinemaService.getAllCinemas(pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "fetching cinemas was successful",
                        cinemaResponse
                )
        );

    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CinemaResponse>>> searchMovies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,

            @PageableDefault(
                    size = 10,
                    page = 0,
                    sort = "id",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable

    ) {

        Page<CinemaResponse> cinema;

        if (name != null && !name.isBlank()) {
            cinema = cinemaService.searchCinemaByName(name, pageable);

        } else if (address != null && !address.isBlank()) {
            cinema = cinemaService.searchCinemaByAddress(address, pageable);



        } else {
            cinema = cinemaService.getAllCinemas(pageable);
        }

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "cinema retrieved successfully",
                        cinema
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CinemaResponse>> getCinemaById(@PathVariable UUID id){
        CinemaResponse response = cinemaService.getCinemaById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "fetching cinema with id was successful",
                        response
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CinemaResponse>> createCinema(  @Valid @RequestBody CreateCinemaRequest request){
        CinemaResponse response = cinemaService.createCinema(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "cinema created successfully",
                        response
                )
        );

    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CinemaResponse>> updateCinema(@PathVariable UUID id ,   @RequestBody  UpdateCinemaRequest request){
        CinemaResponse response = cinemaService.updateCinema(id ,request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "cinema updated successfully",
                        response
                )
        );

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCinema(@PathVariable UUID id) {
        cinemaService.deleteCinemaById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Cinema deleted successfully",
                        null
                )
        );
    }




}
