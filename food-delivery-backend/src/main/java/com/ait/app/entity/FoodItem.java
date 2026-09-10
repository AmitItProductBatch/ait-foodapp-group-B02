package com.ait.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;


@Entity
@Data
public class FoodItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private int foodid;
	
	@Column(nullable = false)
	private String foodname;
	
	@Column(nullable = false)
	private String foodtype;
	
	@Column(nullable = false)
	private String description;
	
	@Column(nullable = false)
	private String cuisine;
	
	@Column(nullable = false)
	private boolean available;
	
	 @Column(nullable = false)
	    private double price;
	
	@ManyToOne
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;
	
	
	
}
