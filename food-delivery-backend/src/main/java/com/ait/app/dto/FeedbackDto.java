package com.ait.app.dto;

import lombok.Data;

@Data
public class FeedbackDto {

	private int id;
	private int userId;
	private int restaurantId;
	private int orderId;
	private int rating;
	private String comment;

}
