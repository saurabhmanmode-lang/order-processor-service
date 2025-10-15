package com.example.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;


public record OrderItemRequestDTO(@NotNull Long productId,
                                  @NotBlank String productName,
                                  @Positive Integer quantity,
                                  @Positive BigDecimal price) {
}
