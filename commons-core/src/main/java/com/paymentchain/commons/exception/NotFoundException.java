package com.paymentchain.commons.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Entity Not Found");
    }

    public NotFoundException(String message) {
        super(message);
    }

}
