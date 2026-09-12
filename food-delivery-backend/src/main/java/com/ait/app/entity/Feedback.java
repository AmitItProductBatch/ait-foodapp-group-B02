package com.ait.app.entity;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "feedback")
@Data
public class Feedback {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;

	    private int userId;

	    private Long restaurantId;

	    private Integer foodItemId;

	    private int rating;

	    private String comment;

	    private LocalDateTime createdAt;

	    private LocalDateTime updatedAt;

}
