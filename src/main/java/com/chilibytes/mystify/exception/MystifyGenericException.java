package com.chilibytes.mystify.exception;

public class MystifyGenericException extends RuntimeException {
    public MystifyGenericException(String message) {
        super(message);
    }

    public MystifyGenericException(String message, Throwable cause) {
        super(message, cause);
    }
}
