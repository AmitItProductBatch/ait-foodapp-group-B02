package com.ait.app.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ait.app.exception.CartItemServiceException;
import com.ait.app.exception.CartServiceException;
import com.ait.app.exception.FeedbackException;
import com.ait.app.exception.FooditemException;
import com.ait.app.exception.PaymentException;
import com.ait.app.exception.RestaurantAddressException;
import com.ait.app.exception.RestaurantException;

import com.ait.app.exception.UserAddressException;
import com.ait.app.exception.UserException;

@ControllerAdvice
public class GlobalExceptionHandling {

	@ExceptionHandler(UserException.class)
	public ResponseEntity<String> handleUserException(UserException userException) {

		return new ResponseEntity(userException.getMessage(), userException.getHttpStatus());
	}

	@ExceptionHandler(FooditemException.class)
	public ResponseEntity<String> handlefooditemException(FooditemException foodexception) {
		return new ResponseEntity(foodexception.getMessage(), foodexception.getHttpStatus());

	}

	@ExceptionHandler(FeedbackException.class)
	public ResponseEntity<String> handleFeedbackException(FeedbackException feedbackException) {

		return new ResponseEntity<>(feedbackException.getMessage(), feedbackException.getHttpStatus());
	}

	@ExceptionHandler(CartServiceException.class)
	public ResponseEntity<String> handleCartServiceException(CartServiceException cartServiceException) {

		return new ResponseEntity(cartServiceException.getMessage(), cartServiceException.getHttpStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception exception) {

		return new ResponseEntity("Something went wrong", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserAddressException.class)
	public ResponseEntity<String> handleUserAddressServiceException(UserAddressException userAddressException) {

		return new ResponseEntity(userAddressException.getMessage(), userAddressException.getHttpStatus());
	}

	@ExceptionHandler(CartItemServiceException.class)
	public ResponseEntity<String> handleCartItemServiceException(CartItemServiceException cartItemServiceException) {

		return new ResponseEntity<>(cartItemServiceException.getMessage(), cartItemServiceException.getHttpStatus());
	}
}
