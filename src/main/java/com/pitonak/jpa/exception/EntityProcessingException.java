package com.pitonak.jpa.exception;

/**
 * Entity processing related exception
 * 
 * @author pitonakt
 */
@SuppressWarnings("serial")
public class EntityProcessingException extends RuntimeException {

    public EntityProcessingException(String message,
                                     Throwable cause) {
        super(message,
                cause);
    }
}
