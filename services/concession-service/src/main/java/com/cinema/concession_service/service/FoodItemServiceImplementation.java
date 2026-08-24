package com.cinema.concession_service.service;



import com.cinema.concession_service.dto.CreateFoodItemRequest;
import com.cinema.concession_service.dto.FoodItemResponse;
import com.cinema.concession_service.entity.FoodItem;
import com.cinema.concession_service.exception.FoodItemNotFoundException;
import com.cinema.concession_service.repository.FoodItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FoodItemServiceImplementation
        implements FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemServiceImplementation(
            FoodItemRepository foodItemRepository
    ) {
        this.foodItemRepository = foodItemRepository;
    }

    @Override
    public FoodItemResponse createFoodItem(
            CreateFoodItemRequest request
    ) {
        log.info("Creating food item with name: {}", request.name());

        FoodItem foodItem = new FoodItem();

        foodItem.setName(request.name());
        foodItem.setDescription(request.description());
        foodItem.setPrice(request.price());
        foodItem.setStockQuantity(request.stockQuantity());
        foodItem.setAvailable(request.stockQuantity() > 0);

        log.info("Saving food item: {}", foodItem);

        FoodItem saved =
                foodItemRepository.save(foodItem);

        return mapToResponse(saved);
    }

    @Override
    public FoodItemResponse getFoodItemById(UUID id) {

        FoodItem foodItem =
                foodItemRepository.findById(id)
                        .orElseThrow(() ->
                                new FoodItemNotFoundException(
                                        "Food item not found: " + id
                                )
                        );

        log.info("Retrieving food item: {}", foodItem);
        return mapToResponse(foodItem);
    }

    @Override
    public List<FoodItemResponse> getAllFoodItems() {

        log.info("Retrieving all food items");
        return foodItemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public FoodItemResponse updateFoodItem(
            UUID id,
            CreateFoodItemRequest request
    ) {
        log.info("Updating food item with id: {}", id);

        FoodItem foodItem =
                foodItemRepository.findById(id)
                        .orElseThrow(() ->
                                new FoodItemNotFoundException(
                                        "Food item not found: " + id
                                )
                        );

        foodItem.setName(request.name());
        foodItem.setDescription(request.description());
        foodItem.setPrice(request.price());
        foodItem.setStockQuantity(request.stockQuantity());
        foodItem.setAvailable(request.stockQuantity() > 0);
        log.info("Saving updated food item: {}", foodItem);

        return mapToResponse(
                foodItemRepository.save(foodItem)
        );
    }

    @Override
    public void deleteFoodItem(UUID id) {
        log.info("Deleting food item with id: {}", id);

        FoodItem foodItem =
                foodItemRepository.findById(id)
                        .orElseThrow(() ->
                                new FoodItemNotFoundException(
                                        "Food item not found: " + id
                                )
                        );

        foodItemRepository.delete(foodItem);
        log.info("Food item with id {} deleted successfully", id);
    }

    private FoodItemResponse mapToResponse(
            FoodItem foodItem
    ) {

        return new FoodItemResponse(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getDescription(),
                foodItem.getPrice(),
                foodItem.getStockQuantity(),
                foodItem.getAvailable(),
                foodItem.getCreatedAt()
        );
    }
}
