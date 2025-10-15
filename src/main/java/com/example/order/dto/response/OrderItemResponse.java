package com.example.order.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(Long productId,
                                String productName,
                                Integer quantity,
                                BigDecimal price) {

}
