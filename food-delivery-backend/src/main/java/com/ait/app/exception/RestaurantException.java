package com.ait.app.exception;

import org.springframework.http.HttpStatus;

public class RestaurantException extends RuntimeException {
	
	private String message;
	private HttpStatus httpStatus;
	
	public RestaurantException(String message, HttpStatus httpStatus) {
		this.message=message;
		this.httpStatus=httpStatus;
	}
	
	public String getMessage() {
		return message;
	}
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

}
