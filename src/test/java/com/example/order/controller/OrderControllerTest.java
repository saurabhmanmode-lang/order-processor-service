package com.example.order.controller;

import com.example.order.dto.enums.OrderStatus;
import com.example.order.dto.request.OrderItemRequestDTO;
import com.example.order.dto.request.OrderRequest;
import com.example.order.dto.response.OrderItemResponse;
import com.example.order.dto.response.OrderResponse;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import com.example.order.utils.OrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        List<OrderItemResponse> items = List.of(
                new OrderItemResponse(1L, "Laptop", 1, BigDecimal.valueOf(1000))
        );

        orderResponse = new OrderResponse(
                1L,
                123L,
                OrderStatus.PENDING,
                BigDecimal.valueOf(1000),
                LocalDateTime.now(),
                items
        );
    }


    @Test
    void testCreateOrder() throws Exception {
        OrderItemRequestDTO item = new OrderItemRequestDTO(1L, "Laptop", 1, BigDecimal.valueOf(1000));
        OrderRequest orderRequest = new OrderRequest(List.of(item));

        Mockito.when(orderService.create(Mockito.<Order>any())).thenReturn(orderResponse);

        mockMvc.perform(post("/api/order")
                        .header("X-Customer-Id", 123L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.status").value("pending"))
                .andExpect(jsonPath("$.items[0].productName").value("Laptop"));

        verify(orderService).create(Mockito.<Order>any());
        verifyNoMoreInteractions(orderService);
    }

    // ------------------- GET ORDER BY ID -------------------
    @Test
    void testGetOrderById() throws Exception {
        Mockito.when(orderService.getOrderById(eq(1L))).thenReturn(orderResponse);

        mockMvc.perform(get("/api/order/{orderId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.customerId").value(123L));

        verify(orderService).getOrderById(eq(1L));
        verifyNoMoreInteractions(orderService);
    }

    // ------------------- GET ALL ORDERS WITHOUT STATUS -------------------
    @Test
    void testGetAllOrdersWithoutStatus() throws Exception {
        Mockito.when(orderService.getAllOrders(eq(null), eq(0), eq(10)))
                .thenReturn(List.of(orderResponse));

        mockMvc.perform(get("/api/order")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1L))
                .andExpect(jsonPath("$[0].status").value("pending"));

        verify(orderService).getAllOrders(eq(null), eq(0), eq(10));
        verifyNoMoreInteractions(orderService);
    }


    @Test
    void testGetAllOrdersWithStatus() throws Exception {
        Mockito.when(orderService.getAllOrders(eq(OrderStatus.PENDING), eq(0), eq(10)))
                .thenReturn(List.of(orderResponse));

        mockMvc.perform(get("/api/order")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("pending"));

        verify(orderService).getAllOrders(eq(OrderStatus.PENDING), eq(0), eq(10));
        verifyNoMoreInteractions(orderService);
    }


    @Test
    void testCancelOrder() throws Exception {
        Mockito.when(orderService.cancelOrder(eq(1L), eq(123L))).thenReturn(orderResponse);

        mockMvc.perform(delete("/api/order/{orderId}", 1L)
                        .header("X-Customer-Id", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.status").value("pending"));

        verify(orderService).cancelOrder(eq(1L), eq(123L));
        verifyNoMoreInteractions(orderService);
    }
}
