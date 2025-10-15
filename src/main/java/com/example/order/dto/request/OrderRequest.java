package com.example.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequest(
        @NotEmpty List<@Valid OrderItemRequestDTO> items) {

}
