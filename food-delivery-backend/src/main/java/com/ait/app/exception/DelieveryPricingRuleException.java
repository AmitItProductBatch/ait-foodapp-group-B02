package com.ait.app.exception;

import org.springframework.http.HttpStatus;

public class DelieveryPricingRuleException extends RuntimeException{
	
	private String message;
	private HttpStatus httpStatus;
	
	
	public DelieveryPricingRuleException(String message, HttpStatus httpStatus) {
		super();
		this.message = message;
		this.httpStatus = httpStatus;
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return getMessage();
	}
	
	public HttpStatus getHttpsstatus() {
		return httpStatus;
		
	}

}
