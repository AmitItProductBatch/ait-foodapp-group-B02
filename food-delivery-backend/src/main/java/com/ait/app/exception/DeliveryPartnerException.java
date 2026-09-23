package com.ait.app.exception;

import org.springframework.http.HttpStatus;

public class DeliveryPartnerException extends RuntimeException {

    private String message;

    private HttpStatus httpStatus;

    public DeliveryPartnerException(String message, HttpStatus httpStatus) {

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