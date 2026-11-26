package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class CredentialValidationException extends BaseException{

    public CredentialValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
