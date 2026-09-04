package com.cinema.concession_service.service;



import com.cinema.concession_service.entity.FoodOrder;
import com.cinema.concession_service.entity.FoodOrderStatus;
import com.cinema.concession_service.entity.PaymentStatus;
import com.cinema.concession_service.event.PaymentProcessedEvent;
import com.cinema.concession_service.repository.FoodOrderRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PaymentProcessedListener {

    private final FoodOrderRepository foodOrderRepository;
    private final FoodOrderService foodOrderService;

    public PaymentProcessedListener(
            FoodOrderRepository foodOrderRepository,
            FoodOrderService foodOrderService
    ) {
        this.foodOrderRepository = foodOrderRepository;
        this.foodOrderService = foodOrderService;
    }

    @KafkaListener(
            topics = "payment-events",
            groupId = "concession-service",
            containerFactory = "paymentProcessedKafkaListenerContainerFactory"
    )
    @Transactional
    public void handlePaymentProcessed(
            PaymentProcessedEvent event
    ) {

        log.info(
                "Received PAYMENT_PROCESSED for booking {} with status {}",
                event.bookingId(),
                event.status()
        );

        List<FoodOrder> orders =
                foodOrderRepository.findByBookingId(
                        event.bookingId()
                );

        if (orders.isEmpty()) {
            log.info(
                    "No food orders found for booking {}",
                    event.bookingId()
            );
            return;
        }

        for (FoodOrder order : orders) {

            if (order.getStatus() != FoodOrderStatus.PENDING) {

                log.info(
                        "Ignoring food order {} because status is {}",
                        order.getId(),
                        order.getStatus()
                );

                continue;
            }

            if (event.status() == PaymentStatus.SUCCESS) {

                foodOrderService.updateFoodOrderStatus(
                        order.getId(),
                        FoodOrderStatus.CONFIRMED
                );

                log.info(
                        "Food order {} confirmed after successful payment",
                        order.getId()
                );

            } else if (event.status() == PaymentStatus.FAILED) {

                foodOrderService.cancelFoodOrder(
                        order.getId()
                );

                log.info(
                        "Food order {} cancelled because payment failed",
                        order.getId()
                );
            }
        }
    }
}