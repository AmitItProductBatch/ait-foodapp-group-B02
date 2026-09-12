package com.ait.app.exception;

import org.springframework.http.HttpStatus;

public class PaymentException extends  RuntimeException {
	
	private String msg;
	private HttpStatus httpStatus;
	
	public PaymentException(String msg, HttpStatus httpStatus) {
		this.msg=msg;
		this.httpStatus=httpStatus;
	}
	
	public String getMessage() {
		return msg;
	}
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

}
