package com.ait.app.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "restaurant_addresses")
public class RestaurantAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private int shopNo;
	
	@Column(nullable = false)
	private String street;
	
	@Column(nullable = false)
	private String area;
	
	@Column(nullable = false)
	private String city;
	
	@Column(nullable=false)
	private String state;
	
	@Column(nullable = false)
	private String pincode;
	
	private double latitude;
	
	private double longitude;
	
	@OneToOne
	@JsonIgnore
	@JoinColumn(name = "restaurant_id")
	private Restaurant restaurant;

	
	
	
	
}
