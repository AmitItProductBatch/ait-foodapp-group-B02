package com.ait.app.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {

	private String transactionId;

    private Long orderId;

    private Long userId;

    private double amount;

    private String paymentMethod;

    private String paymentStatus;
	
}
