package com.cinema.concession_service.service;

import com.cinema.concession_service.dto.CreateFoodOrderRequest;
import com.cinema.concession_service.dto.FoodOrderItemRequest;
import com.cinema.concession_service.dto.FoodOrderItemResponse;
import com.cinema.concession_service.dto.FoodOrderResponse;
import com.cinema.concession_service.entity.FoodItem;
import com.cinema.concession_service.entity.FoodOrder;
import com.cinema.concession_service.entity.FoodOrderItem;
import com.cinema.concession_service.entity.FoodOrderStatus;
import com.cinema.concession_service.event.FoodOrderCreatedEvent;
import com.cinema.concession_service.exception.FoodItemNotFoundException;
import com.cinema.concession_service.exception.FoodOrderNotFoundException;
import com.cinema.concession_service.repository.FoodItemRepository;
import com.cinema.concession_service.repository.FoodOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class FoodOrderServiceImplementation
        implements FoodOrderService {

    private final FoodOrderRepository foodOrderRepository;
    private final FoodItemRepository foodItemRepository;
    private final KafkaConcessionProducer kafkaConcessionProducer;

    public FoodOrderServiceImplementation(
            FoodOrderRepository foodOrderRepository,
            FoodItemRepository foodItemRepository, KafkaConcessionProducer kafkaConcessionProducer
    ) {
        this.foodOrderRepository = foodOrderRepository;
        this.foodItemRepository = foodItemRepository;
        this.kafkaConcessionProducer = kafkaConcessionProducer;
    }

    @Override
    @Transactional
    public FoodOrderResponse createFoodOrder(
            CreateFoodOrderRequest request
    ) {

        log.info(
                "Creating food order for booking {}",
                request.bookingId()
        );

        FoodOrder foodOrder = new FoodOrder();

        foodOrder.setBookingId(request.bookingId());
        foodOrder.setUserId(request.userId());
        foodOrder.setStatus(FoodOrderStatus.PENDING);
        foodOrder.setTotalPrice(BigDecimal.ZERO);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (FoodOrderItemRequest itemRequest : request.items()) {

            FoodItem foodItem =
                    foodItemRepository.findById(
                            itemRequest.foodItemId()
                    ).orElseThrow(() -> {

                        log.warn(
                                "Food item {} not found",
                                itemRequest.foodItemId()
                        );

                        return new FoodItemNotFoundException(
                                "Food item not found: "
                                        + itemRequest.foodItemId()
                        );
                    });

            // Check availability
            if (!Boolean.TRUE.equals(foodItem.getAvailable())) {

                throw new IllegalStateException(
                        "Food item is not available: "
                                + foodItem.getName()
                );
            }

            // Check stock
            if (foodItem.getStockQuantity()
                    < itemRequest.quantity()) {

                throw new IllegalStateException(
                        "Insufficient stock for food item: "
                                + foodItem.getName()
                );
            }

            // Calculate subtotal
            BigDecimal subtotal =
                    foodItem.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemRequest.quantity()
                                    )
                            );

            // Create order item
            FoodOrderItem orderItem =
                    new FoodOrderItem();

            orderItem.setFoodItemId(
                    foodItem.getId()
            );

            orderItem.setFoodItemName(
                    foodItem.getName()
            );

            orderItem.setQuantity(
                    itemRequest.quantity()
            );

            orderItem.setUnitPrice(
                    foodItem.getPrice()
            );

            orderItem.setSubtotal(
                    subtotal
            );


            foodOrder.addItem(orderItem);

            // Decrease stock
            int remainingStock =
                    foodItem.getStockQuantity()
                            - itemRequest.quantity();

            foodItem.setStockQuantity(
                    remainingStock
            );

            foodItem.setAvailable(
                    remainingStock > 0
            );

            foodItemRepository.save(foodItem);

            // Add to order total
            totalPrice =
                    totalPrice.add(subtotal);
        }

        foodOrder.setTotalPrice(totalPrice);

        FoodOrder savedOrder =
                foodOrderRepository.save(foodOrder);

        log.info(
                "Food order {} created successfully for booking {} with total {}",
                savedOrder.getId(),
                savedOrder.getBookingId(),
                savedOrder.getTotalPrice()
        );
        CompletableFuture<SendResult<String , Object>> future = kafkaConcessionProducer.publish(
                savedOrder.getId() ,
                new FoodOrderCreatedEvent(
                        UUID.randomUUID(),
                        savedOrder.getId(),
                        savedOrder.getBookingId(),
                        savedOrder.getUserId(),
                        savedOrder.getTotalPrice(),
                        savedOrder.getStatus()

        ));
        future.thenAccept(result -> log.info(
                "Food order created event published successfully for order {}",
                savedOrder.getId()
        )).exceptionally(ex -> {
            log.error(
                    "Failed to publish food order created event for order {}",
                    savedOrder.getId(),
                    ex
            );
            return null;
        });

        return mapToResponse(savedOrder);
    }


    @Override
    @Transactional(readOnly = true)
    public FoodOrderResponse getFoodOrderById(
            UUID id
    ) {

        log.info(
                "Fetching food order {}",
                id
        );

        FoodOrder foodOrder =
                foodOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new FoodOrderNotFoundException(
                                        "Food order not found: " + id
                                )
                        );

        return mapToResponse(foodOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodOrderResponse> getAllFoodOrders() {

        log.info("Fetching all food orders");

        return foodOrderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodOrderResponse> getFoodOrdersByBookingId(
            UUID bookingId
    ) {

        log.info(
                "Fetching food orders for booking {}",
                bookingId
        );

        return foodOrderRepository
                .findByBookingId(bookingId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodOrderResponse> getFoodOrdersByUserId(
            UUID userId
    ) {

        log.info(
                "Fetching food orders for user {}",
                userId
        );

        return foodOrderRepository
                .findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public FoodOrderResponse updateFoodOrderStatus(
            UUID id,
            FoodOrderStatus status
    ) {

        log.info(
                "Updating food order {} status to {}",
                id,
                status
        );

        FoodOrder foodOrder =
                foodOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new FoodOrderNotFoundException(
                                        "Food order not found: " + id
                                )
                        );

        foodOrder.setStatus(status);

        FoodOrder savedOrder =
                foodOrderRepository.save(foodOrder);

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional
    public void cancelFoodOrder(UUID id) {

        log.info(
                "Cancelling food order {}",
                id
        );

        FoodOrder foodOrder =
                foodOrderRepository.findById(id)
                        .orElseThrow(() ->
                                new FoodOrderNotFoundException(
                                        "Food order not found: " + id
                                )
                        );

        if (foodOrder.getStatus() == FoodOrderStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Completed food orders cannot be cancelled"
            );
        }

        if (foodOrder.getStatus() == FoodOrderStatus.CANCELLED) {

            log.warn(
                    "Food order {} is already cancelled",
                    id
            );

            return;
        }

        // Return stock
        for (FoodOrderItem orderItem :
                foodOrder.getItems()) {

            FoodItem foodItem =
                    foodItemRepository.findById(
                            orderItem.getFoodItemId()
                    ).orElse(null);

            if (foodItem != null) {

                int restoredStock =
                        foodItem.getStockQuantity()
                                + orderItem.getQuantity();

                foodItem.setStockQuantity(
                        restoredStock
                );

                foodItem.setAvailable(true);

                foodItemRepository.save(foodItem);
            }
        }

        foodOrder.setStatus(
                FoodOrderStatus.CANCELLED
        );

        foodOrderRepository.save(foodOrder);

        log.info(
                "Food order {} cancelled successfully",
                id
        );
    }

    private FoodOrderResponse mapToResponse(
            FoodOrder foodOrder
    ) {

        List<FoodOrderItemResponse> items =
                foodOrder.getItems()
                        .stream()
                        .map(this::mapToItemResponse)
                        .toList();

        return new FoodOrderResponse(
                foodOrder.getId(),
                foodOrder.getBookingId(),
                foodOrder.getUserId(),
                foodOrder.getTotalPrice(),
                foodOrder.getStatus(),
                items,
                foodOrder.getCreatedAt()
        );
    }

    private FoodOrderItemResponse mapToItemResponse(
            FoodOrderItem item
    ) {

        return new FoodOrderItemResponse(
                item.getFoodItemId(),
                item.getFoodItemName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}
