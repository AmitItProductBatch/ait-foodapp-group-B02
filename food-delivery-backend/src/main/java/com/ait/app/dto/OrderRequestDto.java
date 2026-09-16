package com.ait.app.dto;

import java.util.List;

import lombok.Data;
@Data
public class OrderRequestDto {

    private int userId;

    private Long restaurantId;

    private int deliveryAddressId;

    private String paymentMethod;

    private String couponCode;

    private List<OrderItemRequestDto> items;

}