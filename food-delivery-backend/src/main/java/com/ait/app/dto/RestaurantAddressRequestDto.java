package com.ait.app.dto;

import lombok.Data;

@Data
public class RestaurantAddressRequestDto {

	private int shopNo;
	private String street;
	private String area;
	private String city;
	private String state;
	private String pincode;

	private Long restaurantId;

}