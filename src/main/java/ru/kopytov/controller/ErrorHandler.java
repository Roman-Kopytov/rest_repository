package ru.kopytov.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.kopytov.exception.NoEntityException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoEntityException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleException(Exception ex) {
        return ex.getMessage();
    }
}
