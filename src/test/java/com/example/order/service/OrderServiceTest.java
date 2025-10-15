package com.example.order.service;


import com.example.order.dto.enums.OrderStatus;
import com.example.order.dto.request.OrderItemRequestDTO;
import com.example.order.dto.request.OrderRequest;
import com.example.order.dto.response.OrderItemResponse;
import com.example.order.dto.response.OrderResponse;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.exceptions.BadRequestException;
import com.example.order.exceptions.NotFoundException;
import com.example.order.repository.OrderRepository;
import com.example.order.service.serviceImpl.OrderServiceImpl;
import com.example.order.utils.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderServiceImpl orderProcessingService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderProcessingService = new OrderServiceImpl(orderRepository);
    }

    @Test
    void testCreateOrder() {
        Long customerId = 101L;

        OrderRequest orderRequest = new OrderRequest(List.of(
                new OrderItemRequestDTO(1L, "Product A", 2, BigDecimal.valueOf(100)),
                new OrderItemRequestDTO(2L, "Product B", 1, BigDecimal.valueOf(50))
        ));
        Order order1 = OrderMapper.mapToOrder(customerId, orderRequest);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            order.setCreatedAt(LocalDateTime.now());
            return order;
        });

        OrderResponse response = orderProcessingService.create(order1);

        verify(orderRepository, times(1)).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        // Verify total amount
        assertEquals(BigDecimal.valueOf(250), savedOrder.getTotalAmount());
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertEquals(2, savedOrder.getItems().size());

        // Verify response mapping
        assertEquals(1L, response.orderId());
        assertEquals(customerId, response.customerId());
        assertEquals(2, response.items().size());
    }

    @Test
    void testGetOrderByIdFound() {
        Long orderId = 1L;
        Order order = Order.builder()
                .id(orderId)
                .customerId(101L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .items(List.of(
                        OrderItem.builder()
                                .productId(1L)
                                .productName("Product A")
                                .quantity(1)
                                .price(BigDecimal.valueOf(100))
                                .build()
                ))
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderResponse response = orderProcessingService.getOrderById(orderId);

        assertEquals(orderId, response.orderId());
        assertEquals(1, response.items().size());
    }

    @Test
    void testGetOrderByIdNotFound() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderProcessingService.getOrderById(orderId));
    }

    @Test
    void testCancelOrderSuccessful() {
        Long orderId = 1L;
        Long customerId = 101L;
        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderResponse response = orderProcessingService.cancelOrder(orderId, customerId);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertNotNull(order.getCancelledAt());
        assertEquals(orderId, response.orderId());
    }

    @Test
    void testCancelOrder_orderNotFound_throwsNotFoundException() {
        Long orderId = 100L;
        Long customerId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> orderProcessingService.cancelOrder(orderId, customerId));
        assertEquals("Order not found with ID: 100", ex.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testCancelOrderUnauthorized() {
        Long orderId = 1L;
        Long customerId = 101L;

        Order order = Order.builder()
                .id(orderId)
                .customerId(999L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> orderProcessingService.cancelOrder(orderId, customerId));

        assertEquals("UNAUTHORIZED_CANCEL", exception.getErrorCode());
    }

    @Test
    void testCancelOrderNotPending() {
        Long orderId = 1L;
        Long customerId = 101L;

        Order order = Order.builder()
                .id(orderId)
                .customerId(customerId)
                .status(OrderStatus.SHIPPED)
                .totalAmount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> orderProcessingService.cancelOrder(orderId, customerId));

        assertEquals("ORDER_NOT_CANCELLABLE", exception.getErrorCode());
    }
}
