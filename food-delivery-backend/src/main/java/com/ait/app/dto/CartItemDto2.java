package com.ait.app.dto;

import lombok.Data;

@Data
public class CartItemDto2 {

	private int foodItemId;
	private String foodname;
	private int quantity;
	private double unitPrice;
	private double subtotal;
}
