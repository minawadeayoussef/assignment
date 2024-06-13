package com.boubyan.assignment.exception;

public class UsernameAlreadyTakenException extends RuntimeException
{
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}
