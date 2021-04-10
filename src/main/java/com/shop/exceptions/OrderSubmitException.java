package com.shop.exceptions;

public class OrderSubmitException extends RuntimeException {

    public OrderSubmitException(String message) {
        super(message);
    }
}
