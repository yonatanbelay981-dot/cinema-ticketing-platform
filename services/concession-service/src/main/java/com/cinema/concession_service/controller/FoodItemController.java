package com.cinema.concession_service.controller;

import com.cinema.concession_service.dto.ApiResponse;
import com.cinema.concession_service.dto.CreateFoodItemRequest;
import com.cinema.concession_service.dto.FoodItemResponse;
import com.cinema.concession_service.service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/food/items")
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<FoodItemResponse>> createFoodItem(
            @Valid @RequestBody CreateFoodItemRequest request
    ) {

        FoodItemResponse foodItem =
                foodItemService.createFoodItem(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Food item created successfully",
                                foodItem
                        )
                );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodItemResponse>>> getAllFoodItems() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food items retrieved successfully",
                        foodItemService.getAllFoodItems()
                )
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodItemResponse>> getFoodItemById(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food item retrieved successfully",
                        foodItemService.getFoodItemById(id)
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodItemResponse>> updateFoodItem(
            @PathVariable UUID id,
            @Valid @RequestBody CreateFoodItemRequest request
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food item updated successfully",
                        foodItemService.updateFoodItem(id, request)
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFoodItem(
            @PathVariable UUID id
    ) {

        foodItemService.deleteFoodItem(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food item deleted successfully",
                        null
                )
        );
    }
}