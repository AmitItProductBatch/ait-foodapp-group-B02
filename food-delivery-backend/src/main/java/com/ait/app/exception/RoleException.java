package com.ait.app.exception;

import org.springframework.http.HttpStatus;

public class RoleException extends RuntimeException {

	private String message;

	private HttpStatus httpStatus;

	public RoleException(String message, HttpStatus httpStatus) {
		
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
