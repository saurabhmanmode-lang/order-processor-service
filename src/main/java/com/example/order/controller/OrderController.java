package com.example.order.controller;

import com.example.order.dto.enums.OrderStatus;
import com.example.order.dto.request.OrderRequest;
import com.example.order.dto.response.OrderResponse;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import com.example.order.utils.OrderMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {


    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestHeader("X-Customer-Id") Long customerId, @Valid @RequestBody OrderRequest orderRequest) {
        Order order = OrderMapper.mapToOrder(customerId, orderRequest);
        return orderService.create(order);

    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);


    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllOrders(@RequestParam(required = false) OrderStatus status,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return orderService.getAllOrders(status, page, size);

    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse cancelOrder(@PathVariable Long orderId,
                                     @RequestHeader("X-Customer-Id") Long customerId) {
        return orderService.cancelOrder(orderId, customerId);
    }

}
