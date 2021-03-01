package com.tp.backlogtracker.exceptions;

public class NoChangesMadeException extends Exception {
    public NoChangesMadeException(String message) {
        super(message);
    }

    public NoChangesMadeException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
