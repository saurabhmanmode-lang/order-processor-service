package com.example.order.utils;

import com.example.order.dto.enums.OrderStatus;
import com.example.order.dto.request.OrderItemRequestDTO;
import com.example.order.dto.request.OrderRequest;
import com.example.order.dto.response.OrderItemResponse;
import com.example.order.dto.response.OrderResponse;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static Order mapToOrder(Long customerId, @Valid OrderRequest orderRequest) {
        Order order = Order.builder()
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .totalAmount(calculateTotalAmount(orderRequest.items()))
                .build();
        List<OrderItem> orderItems = mapItems(orderRequest.items(), order);
        order.setItems(orderItems);

        return order;

    }

    private static List<OrderItem> mapItems(@NotEmpty List<@Valid OrderItemRequestDTO> items, Order order) {
        return items.stream()
                .map(item -> OrderItem.builder()
                        .productId(item.productId())
                        .productName(item.productName())
                        .quantity(item.quantity())
                        .price(item.price())
                        .order(order)
                        .build())
                .collect(Collectors.toList());
    }

    private static BigDecimal calculateTotalAmount(@NotEmpty List<OrderItemRequestDTO> items) {
        return items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public static OrderResponse mapToResponse(Order savedOrder) {
        List<OrderItemResponse> itemResponses = savedOrder.getItems().stream().map(i -> new OrderItemResponse(i.getProductId(), i.getProductName(), i.getQuantity(), i.getPrice())).toList(); //

        return new OrderResponse(savedOrder.getId(), savedOrder.getCustomerId(), savedOrder.getStatus(), savedOrder.getTotalAmount(), savedOrder.getCreatedAt(), itemResponses);
    }
}
