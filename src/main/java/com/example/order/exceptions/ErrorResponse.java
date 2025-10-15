package com.example.order.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(String message,
                            LocalDateTime timestamp) {
}
