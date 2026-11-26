package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class EmptyValueException extends BaseException{
    public EmptyValueException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
