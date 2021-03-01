package com.tp.backlogtracker.exceptions;

public class InvalidUserIDException extends Exception {

    public InvalidUserIDException(String message) {
        super(message);
    }

    public InvalidUserIDException(String message, Throwable innerException) {
        super(message, innerException);
    }

}
