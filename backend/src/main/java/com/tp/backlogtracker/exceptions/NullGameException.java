package com.tp.backlogtracker.exceptions;

public class NullGameException extends Exception {
    public NullGameException(String message) {
        super(message);
    }

    public NullGameException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
