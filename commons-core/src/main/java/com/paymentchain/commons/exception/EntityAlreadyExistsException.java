package com.paymentchain.commons.exception;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException() {
        super("The entity already exists");
    }

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

}
