package com.ait.app.dto;

import lombok.Data;

@Data
public class CartItemDto {

	private int cartId;
	private int foodItemId;
	private int quantity;

}
