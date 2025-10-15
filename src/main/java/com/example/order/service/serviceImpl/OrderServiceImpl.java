package com.example.order.service.serviceImpl;

import com.example.order.dto.enums.OrderStatus;
import com.example.order.dto.response.OrderItemResponse;
import com.example.order.dto.response.OrderResponse;
import com.example.order.entity.Order;
import com.example.order.exceptions.BadRequestException;
import com.example.order.exceptions.NotFoundException;
import com.example.order.repository.OrderRepository;
import com.example.order.service.OrderService;
import com.example.order.utils.OrderMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse create(Order order) {
        Order savedOrder = orderRepository.save(order);
        return OrderMapper.mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        return OrderMapper.mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage;
        orderPage = (status != null) ? orderRepository.findByStatus(status, pageable) : orderRepository.findAll(pageable);
        return orderPage.stream().map(this::mapToResponse).toList();
    }

    @Override
    public OrderResponse cancelOrder(Long orderId, Long customerId) {
        /* assuming customer id with order */
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        validateOrderCancellable(order, customerId);
        order.cancel();
        orderRepository.save(order);
        return OrderMapper.mapToResponse(order);
    }

    private void validateOrderCancellable(Order order, Long customerId) {
        if (!order.getCustomerId().equals(customerId)) {
            throw new BadRequestException("You are not authorized to cancel this order", "UNAUTHORIZED_CANCEL");
        }


        if (!order.isPending()) {
            throw new BadRequestException("Order cannot be canceled because it is already " + order.getStatus(), "ORDER_NOT_CANCELLABLE");
        }
    }
    private OrderResponse mapToResponse(Order savedOrder) {
        List<OrderItemResponse> itemResponses = savedOrder.getItems().stream().map(i -> new OrderItemResponse(i.getProductId(), i.getProductName(), i.getQuantity(), i.getPrice())).toList(); //

        return new OrderResponse(savedOrder.getId(), savedOrder.getCustomerId(), savedOrder.getStatus(), savedOrder.getTotalAmount(), savedOrder.getCreatedAt(), itemResponses);
    }

}
