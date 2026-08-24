package com.cinema.concession_service.controller;



import com.cinema.concession_service.dto.ApiResponse;
import com.cinema.concession_service.dto.CreateFoodOrderRequest;
import com.cinema.concession_service.dto.FoodOrderResponse;
import com.cinema.concession_service.entity.FoodOrderStatus;
import com.cinema.concession_service.service.FoodOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/food/orders")
public class FoodOrderController {

    private final FoodOrderService foodOrderService;

    public FoodOrderController(
            FoodOrderService foodOrderService
    ) {
        this.foodOrderService = foodOrderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoodOrderResponse>>
    createFoodOrder(
            @Valid @RequestBody CreateFoodOrderRequest request
    ) {

        FoodOrderResponse order =
                foodOrderService.createFoodOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Food order created successfully",
                                order
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodOrderResponse>>>
    getAllFoodOrders() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food orders retrieved successfully",
                        foodOrderService.getAllFoodOrders()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodOrderResponse>>
    getFoodOrderById(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food order retrieved successfully",
                        foodOrderService.getFoodOrderById(id)
                )
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<FoodOrderResponse>>>
    getFoodOrdersByBookingId(
            @PathVariable UUID bookingId
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food orders retrieved successfully",
                        foodOrderService
                                .getFoodOrdersByBookingId(bookingId)
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FoodOrderResponse>>>
    getFoodOrdersByUserId(
            @PathVariable UUID userId
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food orders retrieved successfully",
                        foodOrderService
                                .getFoodOrdersByUserId(userId)
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<FoodOrderResponse>>
    updateFoodOrderStatus(
            @PathVariable UUID id,
            @RequestParam FoodOrderStatus status
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food order status updated successfully",
                        foodOrderService.updateFoodOrderStatus(
                                id,
                                status
                        )
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>>
    cancelFoodOrder(
            @PathVariable UUID id
    ) {

        foodOrderService.cancelFoodOrder(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food order cancelled successfully",
                        null
                )
        );
    }
}
