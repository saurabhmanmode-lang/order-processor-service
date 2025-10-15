package com.example.order.exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String errorCode;

    public NotFoundException(String message) {
        super(message);
        this.errorCode = "ORDER_NOT_FOUND";
    }

    public NotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
