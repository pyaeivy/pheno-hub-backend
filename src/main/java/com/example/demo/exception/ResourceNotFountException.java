package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFountException extends BaseException{
    public ResourceNotFountException(String message) {
        super(HttpStatus.NOT_FOUND,message);
    }
}
