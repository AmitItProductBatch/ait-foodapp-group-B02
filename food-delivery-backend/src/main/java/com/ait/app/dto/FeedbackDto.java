package com.ait.app.dto;

import lombok.Data;

@Data
public class FeedbackDto {

    private int userId;

    private Long restaurantId;

    private Integer foodItemId;

    private int rating;

    private String comment;

}
