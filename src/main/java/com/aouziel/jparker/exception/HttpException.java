package com.aouziel.jparker.exception;

import org.springframework.http.HttpStatus;

public class HttpException extends Exception {
    private final HttpStatus status;

    public HttpException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
