package ru.kopytov.exception;

public class NoEntityException extends RuntimeException {

    public NoEntityException(String message) {
        super(message);
    }
}