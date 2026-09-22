package com.ait.app.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ait.app.exception.CartItemServiceException;
import com.ait.app.exception.CartServiceException;
import com.ait.app.exception.DelieveryPricingRuleException;
import com.ait.app.exception.DeliveryFeeException;
import com.ait.app.exception.FeedbackException;
import com.ait.app.exception.FooditemException;
import com.ait.app.exception.OrderException;
import com.ait.app.exception.PaymentException;
import com.ait.app.exception.RestaurantAddressException;
import com.ait.app.exception.RestaurantException;
import com.ait.app.exception.RoleException;
import com.ait.app.exception.UserAddressException;
import com.ait.app.exception.UserException;

@ControllerAdvice
public class GlobalExceptionHandling {

	@ExceptionHandler(UserException.class)
	public ResponseEntity handleUserException(UserException userException) {

		return new ResponseEntity(userException.getMessage(), userException.getHttpStatus());
	}

	@ExceptionHandler(FooditemException.class)
	public ResponseEntity handleFooditemException(FooditemException fooditemException) {

		return new ResponseEntity(fooditemException.getMessage(), fooditemException.getHttpStatus());
	}

	@ExceptionHandler(RestaurantException.class)
	public ResponseEntity handleRestaurantException(RestaurantException restaurantException) {

		return new ResponseEntity(restaurantException.getMessage(), restaurantException.getHttpStatus());
	}

	@ExceptionHandler(RestaurantAddressException.class)
	public ResponseEntity handleRestaurantAddressException(RestaurantAddressException restaurantAddressException) {

		return new ResponseEntity(restaurantAddressException.getMessage(), restaurantAddressException.getHttpStatus());
	}

	@ExceptionHandler(PaymentException.class)
	public ResponseEntity handlePaymentException(PaymentException paymentException) {

		return new ResponseEntity(paymentException.getMessage(), paymentException.getHttpStatus());
	}

	@ExceptionHandler(FeedbackException.class)
	public ResponseEntity handleFeedbackException(FeedbackException feedbackException) {

		return new ResponseEntity(feedbackException.getMessage(), feedbackException.getHttpStatus());
	}

	@ExceptionHandler(CartServiceException.class)
	public ResponseEntity handleCartServiceException(CartServiceException cartServiceException) {

		return new ResponseEntity(cartServiceException.getMessage(), cartServiceException.getHttpStatus());
	}

	@ExceptionHandler(CartItemServiceException.class)
	public ResponseEntity handleCartItemServiceException(CartItemServiceException cartItemServiceException) {

		return new ResponseEntity(cartItemServiceException.getMessage(), cartItemServiceException.getHttpStatus());
	}

	@ExceptionHandler(OrderException.class)
	public ResponseEntity handleOrderException(OrderException orderException) {

		return new ResponseEntity(orderException.getMessage(), orderException.getHttpStatus());
	}

	@ExceptionHandler(DeliveryFeeException.class)
	public ResponseEntity handleDeliveryFeeException(DeliveryFeeException deliveryFeeException) {

		return new ResponseEntity(deliveryFeeException.getMessage(), deliveryFeeException.getHttpStatus());
	}

	@ExceptionHandler(UserAddressException.class)
	public ResponseEntity handleUserAddressException(UserAddressException userAddressException) {

		return new ResponseEntity(userAddressException.getMessage(), userAddressException.getHttpStatus());
	}

	@ExceptionHandler(DelieveryPricingRuleException.class)
	public ResponseEntity handleDelieveryPricingRuleException(
			DelieveryPricingRuleException delieveryPricingRuleException) {

		return new ResponseEntity(delieveryPricingRuleException.getMessage(),
				delieveryPricingRuleException.getHttpStatus());
	}

	@ExceptionHandler(RoleException.class)
	public ResponseEntity handleRoleException(RoleException roleException) {

		return new ResponseEntity(roleException.getMessage(), roleException.getHttpStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity handleException(Exception exception) {

		return new ResponseEntity("Internal server error: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
