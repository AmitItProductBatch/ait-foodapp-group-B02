package com.ait.app.exception;

import org.springframework.http.HttpStatus;

public class FooditemException extends RuntimeException{

	private String message;
	private HttpStatus httpStatus;

	

	public FooditemException(String message, HttpStatus httpStatus) {
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
