package com.cinema.schedule_service.controller;

import com.cinema.schedule_service.dto.ApiResponse;
import com.cinema.schedule_service.dto.CreateShowtimeRequest;
import com.cinema.schedule_service.dto.ShowtimeResponse;
import com.cinema.schedule_service.dto.UpdateShowtimeRequest;
import com.cinema.schedule_service.service.ShowTimeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/showtimes")
public class ShowTimeController {
    private  final ShowTimeService showTimeService;

    public ShowTimeController(ShowTimeService showTimeService) {
        this.showTimeService = showTimeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ShowtimeResponse>>> getAllShowTimes(@PageableDefault(page = 0 , size = 10  , sort = "id" , direction = Sort.Direction.DESC) Pageable pageable){
        Page<ShowtimeResponse> showtimeResponses = showTimeService.getAllSHowTime(pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "fetching showTime is successful",
                        showtimeResponses

                )
        );

    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ShowtimeResponse>> createShowTime( @Valid @RequestBody CreateShowtimeRequest request){
        ShowtimeResponse response   = showTimeService.createShowTime(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "creating showTime is successful",
                        response
                )
        );
    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ShowtimeResponse>>> searchShowTimes(
            @RequestParam(required = false) UUID movieId,
            @RequestParam(required = false) UUID hallId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @PageableDefault(page = 0 , size = 10  , sort = "id" , direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<ShowtimeResponse> showtimeResponses;
        if(movieId != null){
            showtimeResponses = showTimeService.searchByMovieId(movieId, pageable);
        }else if(hallId != null){
            showtimeResponses = showTimeService.searchByHallId(hallId, pageable);
        }else if(startTime != null && endTime != null){
            showtimeResponses = showTimeService.searchByStartTimeBetween(
                    java.time.LocalDateTime.parse(startTime),
                    java.time.LocalDateTime.parse(endTime),
                    pageable
            );
        }else{
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            false,
                            "invalid search parameters",
                            null
                    )
            );
        }
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "searching showTimes is successful",
                        showtimeResponses
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> getShowTimeById(@PathVariable UUID id){
        ShowtimeResponse response = showTimeService.getShowTimeById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "fetching showTime is successful",
                        response
                )
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> updateShowTime(@PathVariable UUID id, @Valid @RequestBody UpdateShowtimeRequest request){
        ShowtimeResponse response = showTimeService.updateShowTime(id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "updating showTime is successful",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShowTime(@PathVariable UUID id){
        showTimeService.deleteShowTimeById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "deleting showTime is successful",
                        null
                )
        );
    }


}
