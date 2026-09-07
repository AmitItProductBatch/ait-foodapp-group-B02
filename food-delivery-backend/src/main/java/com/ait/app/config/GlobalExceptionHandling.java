package com.ait.app.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ait.app.exception.UserException;

@ControllerAdvice
public class GlobalExceptionHandling {
	
	@ExceptionHandler(UserException.class)
	public ResponseEntity<String> handleUserException(UserException userException) {

		return new ResponseEntity(userException.getMessage(), userException.getHttpStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception exception) {

		return new ResponseEntity("Something went wrong", HttpStatus.BAD_REQUEST);
	}

}
