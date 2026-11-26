package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException{
    public ResourceAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
