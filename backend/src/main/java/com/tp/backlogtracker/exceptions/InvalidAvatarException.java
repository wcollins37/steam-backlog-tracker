package com.tp.backlogtracker.exceptions;

public class InvalidAvatarException extends Exception {

    public InvalidAvatarException(String message) {
        super(message);
    }

    public InvalidAvatarException(String message, Throwable innerException) {
        super(message, innerException);
    }

}
