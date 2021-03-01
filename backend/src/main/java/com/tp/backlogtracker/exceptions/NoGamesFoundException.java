package com.tp.backlogtracker.exceptions;

public class NoGamesFoundException extends Exception {
    public NoGamesFoundException(String message) {
        super(message);
    }

    public NoGamesFoundException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
