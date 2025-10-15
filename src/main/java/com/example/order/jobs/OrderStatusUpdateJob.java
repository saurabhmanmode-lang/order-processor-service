package com.example.order.jobs;

import com.example.order.dto.enums.OrderStatus;
import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusUpdateJob {

    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 5 * 60000)
    @Transactional
    public void updatePendingOrdersToProcessing() {
        log.info("⏰ Order status update job started at {}", LocalDateTime.now());

        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);

        if (pendingOrders.isEmpty()) {
            log.info("No pending orders found for processing.");
            return;
        }

        pendingOrders.forEach(order -> {
            order.setStatus(OrderStatus.PROCESSING);
            order.setUpdatedAt(LocalDateTime.now());
        });

        orderRepository.saveAll(pendingOrders);

        log.info("✅ Updated {} orders from PENDING to PROCESSING.", pendingOrders.size());
    }
}
