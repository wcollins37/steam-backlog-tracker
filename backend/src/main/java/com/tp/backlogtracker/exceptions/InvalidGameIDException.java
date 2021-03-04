package com.tp.backlogtracker.exceptions;

public class InvalidGameIDException extends Exception {

    public InvalidGameIDException(String message) {
        super(message);
    }

    public InvalidGameIDException(String message, Throwable innerException) {
        super(message, innerException);
    }

}