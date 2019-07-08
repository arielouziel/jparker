package com.aouziel.jparker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
public class PreconditionFailedException extends HttpException {
    private static final long serialVersionUID = 1L;

    public PreconditionFailedException(String message) {
        super(HttpStatus.PRECONDITION_FAILED, message);
    }
}
