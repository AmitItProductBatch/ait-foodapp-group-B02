package com.ait.app.exception;

import org.springframework.http.HttpStatus;

public class FeedbackException extends RuntimeException {

    private String message;
    private HttpStatus httpStatus;

    public FeedbackException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}