package com.ait.app.exception;

import org.springframework.http.HttpStatus;

public class UserException extends RuntimeException{

	private String message;
	private HttpStatus httpStatus;

	public UserException(String message, HttpStatus httpStatus) {
		this.message = message;
		this.httpStatus = httpStatus;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
