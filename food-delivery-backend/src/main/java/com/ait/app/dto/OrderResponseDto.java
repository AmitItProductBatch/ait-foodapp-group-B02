package com.ait.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderResponseDto {

    private Long orderId;

    private Long userId;

    private Long restaurantId;

    private List<OrderItemResponseDto> items;

    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal tax;

    private BigDecimal deliveryFee;

    private BigDecimal packagingFee;

    private BigDecimal totalAmount;

    private String orderStatus;

    private String paymentStatus;

    private LocalDateTime createdAt;

   
}