package com.cinema.concession_service.controller;

import com.cinema.concession_service.dto.ApiResponse;
import com.cinema.concession_service.dto.CreateFoodOrderRequest;
import com.cinema.concession_service.dto.FoodOrderResponse;
import com.cinema.concession_service.entity.FoodOrderStatus;
import com.cinema.concession_service.service.FoodOrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/food/orders")
@Slf4j
public class FoodOrderController {

    private final FoodOrderService foodOrderService;

    public FoodOrderController(
            FoodOrderService foodOrderService
    ) {
        this.foodOrderService = foodOrderService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ApiResponse<FoodOrderResponse>> createFoodOrder(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateFoodOrderRequest request
    ) {
        log.info("JWT claims: {}", jwt.getClaims());

        UUID userId = UUID.fromString(jwt.getSubject());

        FoodOrderResponse order =
                foodOrderService.createFoodOrder(
                        userId,
                        request
                );

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

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@foodOrderSecurity.isOwner(#id, authentication)"
    )
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

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@foodOrderSecurity.ownsBooking(#bookingId, authentication)"
    )
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<FoodOrderResponse>>>
    getFoodOrdersByBookingId(
            @PathVariable UUID bookingId
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food orders retrieved successfully",
                        foodOrderService.getFoodOrdersByBookingId(
                                bookingId
                        )
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FoodOrderResponse>>>
    getFoodOrdersByUserId(
            @PathVariable UUID userId
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Food orders retrieved successfully",
                        foodOrderService.getFoodOrdersByUserId(
                                userId
                        )
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@foodOrderSecurity.isOwner(#id, authentication)"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelFoodOrder(
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