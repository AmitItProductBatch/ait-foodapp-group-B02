package com.ait.app.dto;

import java.util.List;

import lombok.Data;

@Data
public class CartResponse3 {

	    private int cartId;
	    private int userId;
	    private String restaurantName;
	    private String userName;
	    private String userMobile;
	    private List<CartItemDto2> items;
	    private double totalAmount;
}
