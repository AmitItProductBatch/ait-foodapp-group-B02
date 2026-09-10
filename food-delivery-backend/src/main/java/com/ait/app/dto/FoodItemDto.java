package com.ait.app.dto;

import lombok.Data;

@Data
public class FoodItemDto {
	
	
	private String foodname;
	private String foodtype;
	private String description;
	private String cuisine;
	private boolean available;
	private double price;
   private int restaurantId;

}
