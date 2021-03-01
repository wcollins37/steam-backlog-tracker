package com.tp.backlogtracker.exceptions;

public class InvalidUserNameException extends Exception {

    public InvalidUserNameException(String message) {
        super(message);
    }

    public InvalidUserNameException(String message, Throwable innerException) {
        super(message, innerException);
    }

}