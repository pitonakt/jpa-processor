package com.pitonak.jpa.exception;

public class EntityProcessingException extends RuntimeException {

    public EntityProcessingException(String message,
                                     Throwable cause) {
        super(message,
                cause);
    }
}
