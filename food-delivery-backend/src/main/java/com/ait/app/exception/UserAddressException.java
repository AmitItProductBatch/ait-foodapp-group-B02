package com.ait.app.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class UserAddressException extends RuntimeException{

	private String message;
	
	private HttpStatus httpStatus;

	

	public UserAddressException(String message, HttpStatus httpStatus) {
		super();
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