package com.cinema.concession_service.service;


import com.cinema.concession_service.dto.CreateFoodItemRequest;
import com.cinema.concession_service.dto.FoodItemResponse;

import java.util.List;
import java.util.UUID;

public interface FoodItemService {

    FoodItemResponse createFoodItem(
            CreateFoodItemRequest request
    );

    FoodItemResponse getFoodItemById(
            UUID id
    );

    List<FoodItemResponse> getAllFoodItems();

    FoodItemResponse updateFoodItem(
            UUID id,
            CreateFoodItemRequest request
    );

    void deleteFoodItem(UUID id);
}
