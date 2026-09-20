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
import com.ait.app.exception.UserAddressException;
import com.ait.app.exception.UserException;

@ControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> handleUserException(UserException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(FooditemException.class)
    public ResponseEntity<String> handleFooditemException(FooditemException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(RestaurantException.class)
    public ResponseEntity<String> handleRestaurantException(RestaurantException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(RestaurantAddressException.class)
    public ResponseEntity<String> handleRestaurantAddressException(RestaurantAddressException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<String> handlePaymentException(PaymentException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(FeedbackException.class)
    public ResponseEntity<String> handleFeedbackException(FeedbackException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(CartServiceException.class)
    public ResponseEntity<String> handleCartServiceException(CartServiceException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(CartItemServiceException.class)
    public ResponseEntity<String> handleCartItemServiceException(CartItemServiceException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<String> handleOrderException(OrderException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(DeliveryFeeException.class)
    public ResponseEntity<String> handleDeliveryFeeException(DeliveryFeeException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(UserAddressException.class)
    public ResponseEntity<String> handleUserAddressException(UserAddressException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(DelieveryPricingRuleException.class)
    public ResponseEntity<String> handleDelieveryPricingRuleException(DelieveryPricingRuleException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>("Internal server error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
