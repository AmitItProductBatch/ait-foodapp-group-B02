package com.ait.app.dto;

import java.util.List;

import lombok.Data;

@Data
public class CartResponse2 {

	 private int cartId;
	 private Long restaurantId;
	 private String restaurantName;
	 private List<CartItemDto2> items;
	 private double totalAmount;
}
