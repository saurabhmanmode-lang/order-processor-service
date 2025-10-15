package com.example.order.service;

import com.example.order.dto.enums.OrderStatus;
import com.example.order.dto.response.OrderResponse;
import com.example.order.entity.Order;

import java.util.List;

public interface OrderService {

    OrderResponse create(Order order);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getAllOrders(OrderStatus status,int page, int size);
    OrderResponse cancelOrder(Long orderId, Long customerId);
}
