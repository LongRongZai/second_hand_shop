package com.shop.exceptions;

public class SendMailException extends RuntimeException {
    public SendMailException(String message) {
        super(message);
    }

}
