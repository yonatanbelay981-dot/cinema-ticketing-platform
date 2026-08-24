package com.cinema.concession_service.service;



import com.cinema.concession_service.dto.CreateFoodOrderRequest;
import com.cinema.concession_service.dto.FoodOrderResponse;
import com.cinema.concession_service.entity.FoodOrderStatus;

import java.util.List;
import java.util.UUID;

public interface FoodOrderService {

    FoodOrderResponse createFoodOrder(
            CreateFoodOrderRequest request
    );

    FoodOrderResponse getFoodOrderById(
            UUID id
    );

    List<FoodOrderResponse> getAllFoodOrders();

    List<FoodOrderResponse> getFoodOrdersByBookingId(
            UUID bookingId
    );

    List<FoodOrderResponse> getFoodOrdersByUserId(
            UUID userId
    );

    FoodOrderResponse updateFoodOrderStatus(
            UUID id,
            FoodOrderStatus status
    );

    void cancelFoodOrder(UUID id);
}
